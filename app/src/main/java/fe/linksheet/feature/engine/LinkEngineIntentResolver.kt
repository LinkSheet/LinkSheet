package fe.linksheet.feature.engine

import android.content.Context
import android.content.Intent
import android.net.Uri
import app.linksheet.feature.app.core.PackageIntentHandler
import app.linksheet.feature.app.core.PackageLauncherService
import app.linksheet.feature.browser.core.PrivateBrowsingService
import app.linksheet.feature.engine.core.*
import app.linksheet.feature.engine.core.context.DefaultEngineRunContext
import app.linksheet.feature.engine.core.context.IgnoreLibRedirectExtra
import app.linksheet.feature.engine.core.context.SkipFollowRedirectsExtra
import app.linksheet.feature.engine.core.context.toExtra
import app.linksheet.feature.engine.core.fetcher.ContextResultId
import app.linksheet.feature.engine.core.fetcher.preview.toUnfurlResult
import app.linksheet.feature.engine.core.fetcher.toFetchResult
import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import fe.composekit.lifecycle.network.core.NetworkStateService
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.extension.toAndroidUri
import fe.linksheet.extension.toStdUrl
import fe.linksheet.module.database.dao.base.PackageEntityCreator
import fe.linksheet.module.database.dao.base.WhitelistedBrowsersDao
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.database.entity.whitelisted.WhitelistedBrowser
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.*
import fe.linksheet.module.resolver.ImprovedIntentResolver.Companion.IntentKeyResolveRedirects
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.module.resolver.module.BrowserSettings
import fe.linksheet.module.resolver.module.IntentResolverSettings
import fe.linksheet.module.resolver.util.AppSorter
import fe.linksheet.module.resolver.util.CustomTabHandler
import fe.linksheet.module.resolver.util.CustomTabInfo2
import fe.linksheet.module.resolver.util.IntentSanitizer
import fe.linksheet.util.Scheme
import fe.linksheet.util.getAndroidAppPackage
import fe.linksheet.util.intent.parser.IntentParser
import fe.linksheet.util.intent.parser.UriException
import fe.linksheet.util.intent.parser.UriParseException
import fe.std.result.IResult
import fe.std.result.isFailure
import fe.std.result.unaryPlus
import fe.std.uri.StdUrl
import io.ktor.client.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import mozilla.components.support.base.log.logger.Logger
import mozilla.components.support.utils.SafeIntent

class LinkEngineIntentResolver(
    val context: Context,
    val client: HttpClient,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val normalBrowsersRepository: WhitelistedNormalBrowsersRepository,
    private val inAppBrowsersRepository: WhitelistedInAppBrowsersRepository,
    private val packageIntentHandler: PackageIntentHandler,
    private val packageLauncherService: PackageLauncherService,
    private val appSorter: AppSorter,
    private val browserHandler: ImprovedBrowserHandler,
    private val inAppBrowserHandler: InAppBrowserHandler,
    private val networkStateService: NetworkStateService,
    private val selector: ScenarioSelector,
    private val privateBrowsingService: PrivateBrowsingService,
    private val settings: IntentResolverSettings,
) : IntentResolver {
    private val logger = Logger("LinkEngineIntentResolver")
    private val browserSettings = settings.browserSettings
    private val previewSettings = settings.previewSettings
    private val downloaderSettings = settings.downloaderSettings
    private val libRedirectSettings = settings.libRedirectSettings
    private val amp2HtmlSettings = settings.amp2HtmlSettings
    private val followRedirectsSettings = settings.followRedirectsSettings

    private val _events = MutableStateFlow(value = ResolveEvent.Idle)
    override val events = _events.asStateFlow()

    private val _interactions = MutableStateFlow<ResolverInteraction>(value = ResolverInteraction.Idle)
    override val interactions = _interactions.asStateFlow()

    private fun emitEvent(event: ResolveEvent) {
        _events.tryEmit(event)
        logger.debug(event.toString())
    }

    private fun emitEventIf(predicate: Boolean, event: ResolveEvent) {
        if (!predicate) return
        emitEvent(event)
    }

    private fun emitInteraction(interaction: ResolverInteraction) {
        _interactions.tryEmit(interaction)
        logger.debug("Emitted interaction $interaction")
    }

    private fun clearInteraction() = emitInteraction(ResolverInteraction.Clear)

    private fun fail(error: String, result: IntentResolveResult): IntentResolveResult {
        logger.error(error)
        return result
    }

    private suspend fun initState(event: ResolveEvent, interaction: ResolverInteraction) {
        _events.emit(event)
        _interactions.emit(interaction)
    }

    private fun parseIntent(intent: SafeIntent): IResult<StdUrl> {
        val uriResult = IntentParser.getUriFromIntent(intent)
        if (uriResult.isFailure()) {
            logger.error("Failed to parse intent ${intent.action}")
            return +uriResult
        }

        val url = uriResult.value.toStdUrl() ?: return +UriParseException
        return +url
    }

    override suspend fun resolve(
        intent: SafeIntent,
        referrer: Uri?,
    ): IntentResolveResult = coroutineScope scope@{
        val canAccessInternet = networkStateService.isNetworkConnected
        val urlParseResult = parseIntent(intent)
        if (urlParseResult.isFailure()) {
            return@scope IntentResolveResult.IntentParseFailed(urlParseResult.exception as UriException)
        }

        val startUrl = urlParseResult.value
        val referringPackage = referrer?.getAndroidAppPackage(Scheme.Package)
        val knownBrowser = privateBrowsingService.isKnownBrowser(referringPackage?.packageName)
        val isReferrerBrowser = knownBrowser != null

        emitEvent(ResolveEvent.QueryingBrowsers)
        val browsers = packageIntentHandler.findHttpBrowsable(null)

        val manualResolveRedirects = checkIntentFlag(intent, IntentKeyResolveRedirects, followRedirectsSettings.manualFollowRedirects())
        val ignoreLibRedirect = checkIntentFlag(intent, LibRedirectDefault.IgnoreIntentKey, libRedirectSettings.enableIgnoreLibRedirectButton())

        val context = DefaultEngineRunContext {
            if (ignoreLibRedirect) {
                add(IgnoreLibRedirectExtra)
            }

            if (manualResolveRedirects && (isReferrerBrowser && followRedirectsSettings.followRedirectsSkipBrowser())) {
                add(SkipFollowRedirectsExtra)
            }

            referringPackage?.toExtra()?.let(::add)
            knownBrowser?.toExtra()?.let (::add)
        }

        val input = EngineScenarioInput(startUrl, referringPackage)
        val scenario = selector.findScenario(input)
        logger.info("Found scenario $scenario")
        if (scenario == null) {
            // TODO: What do we do in this situation?
            return@scope IntentResolveResult.NoScenarioFound
        }

        val (sealedContext, result) = scenario.run(startUrl, context)
        if (result is IntentEngineResult) {
            return@scope IntentResolveResult.IntentResult(result.intent)
        }

        if (result is ForwardOtherProfileResult) {
            return@scope IntentResolveResult.OtherProfile(result.url)
        }

        val resultUrl = (result as UrlEngineResult).url
        val resultUri = resultUrl.toAndroidUri()

        val downloadResult = sealedContext[ContextResultId.Download]

        val allowCustomTab = inAppBrowserHandler.shouldAllowCustomTab(referrer, browserSettings.inAppBrowserSettings())
        val customTab = CustomTabHandler.getInfo2(intent, allowCustomTab)
        val newIntent = IntentSanitizer.sanitize(intent, Intent.ACTION_VIEW, resultUri, customTab.dropExtras)

        emitEvent(ResolveEvent.LoadingPreferredApps)
        val app = queryPreferredApp(
            repository = preferredAppRepository,
            packageLauncherService = packageLauncherService,
            uri = resultUri
        )

        val lastUsedApps = queryAppSelectionHistory(
            repository = appSelectionHistoryRepository,
            packageLauncherService = packageLauncherService,
            uri = resultUri
        )
        val resolveList = packageIntentHandler.findHandlers(resultUri, referringPackage?.packageName)

        emitEvent(ResolveEvent.CheckingBrowsers)
        val browserModeConfigHelper = browserSettings.createBrowserModeConfig(customTab is CustomTabInfo2.Allowed)
        val appList = browserHandler.filterBrowsers(
            config = browserModeConfigHelper,
            browsers = browsers,
            resolveList = resolveList
        )

        emitEvent(ResolveEvent.SortingApps)
        val (sorted, filtered) = appSorter.sort(
            appList = appList,
            lastChosen = app,
            historyMap = lastUsedApps,
            returnLastChosen = !settings.dontShowFilteredItem()
        )

        return@scope IntentResolveResult.Default(
            intent = newIntent,
            uri = resultUri,
            unfurlResult = sealedContext[ContextResultId.Preview]?.toUnfurlResult(),
            referringPackageName = referringPackage?.packageName,
            resolved = sorted,
            filteredItem = filtered,
            alwaysPreferred = app?.alwaysPreferred,
            hasSingleMatchingOption = appList.isSingleOption || appList.noBrowsersOnlySingleApp,
            resolveModuleStatus = ResolveModuleStatus(),
            libRedirectResult = sealedContext[ContextResultId.LibRedirect]?.wrapped,
            downloadable = downloadResult?.toFetchResult()
        )
    }

    private suspend fun BrowserSettings.createBrowserModeConfig(
        customTab: Boolean,
    ): BrowserModeConfigHelper {
        if (!unifiedPreferredBrowser() && customTab) {
            return mapToBrowserConfig(
                mode = inAppBrowserMode(),
                selectedInAppBrowser = selectedInAppBrowser(),
                repository = inAppBrowsersRepository
            )
        }

        return mapToBrowserConfig(
            mode = browserMode(),
            selectedInAppBrowser = selectedBrowser(),
            repository = normalBrowsersRepository
        )
    }

    private suspend fun <T : WhitelistedBrowser<T>, C : PackageEntityCreator<T>, D : WhitelistedBrowsersDao<T, C>> mapToBrowserConfig(
        mode: BrowserMode,
        selectedInAppBrowser: String?,
        repository: WhitelistedBrowsersRepository<T, C, D>,
    ): BrowserModeConfigHelper = when (mode) {
        BrowserMode.AlwaysAsk -> BrowserModeConfigHelper.AlwaysAsk
        BrowserMode.None -> BrowserModeConfigHelper.None
        BrowserMode.SelectedBrowser -> BrowserModeConfigHelper.SelectedBrowser(selectedInAppBrowser)
        BrowserMode.Whitelisted -> BrowserModeConfigHelper.Whitelisted(
            repository.getAll().firstOrNull()?.mapToSet { it.packageName }
        )
    }

    private fun checkIntentFlag(intent: SafeIntent, flag: String, setting: Boolean): Boolean {
        if (!intent.getBooleanExtra(flag, false)) return false
        intent.extras?.remove(flag)
        return setting
    }

    private suspend fun queryAppSelectionHistory(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        repository: AppSelectionHistoryRepository,
        packageLauncherService: PackageLauncherService,
        uri: Uri?,
    ): Map<String, Long> = withContext(dispatcher) {
        val lastUsedApps = repository.getLastUsedForHostGroupedByPackage(uri)
            ?: return@withContext emptyMap()

        val (result, delete) = packageLauncherService.hasLauncher(lastUsedApps.keys)
        if (delete.isNotEmpty()) repository.delete(delete)

        lastUsedApps.filter { it.key in result }.toMap()
    }

    private suspend fun queryPreferredApp(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        repository: PreferredAppRepository,
        packageLauncherService: PackageLauncherService,
        uri: Uri?,
    ): PreferredApp? = withContext(dispatcher) {
        val app = repository.getByHost(uri)
        val resolveInfo = packageLauncherService.getLauncherOrNull(app?.pkg)
        if (app != null && resolveInfo == null) repository.delete(app)

        app
    }

    override suspend fun warmup() {
    }
}
