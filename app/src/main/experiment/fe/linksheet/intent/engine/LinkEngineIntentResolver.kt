package fe.linksheet.intent.engine

import android.content.Context
import android.content.Intent
import android.net.Uri
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.experiment.engine.Input
import fe.linksheet.experiment.engine.TrackSelector
import fe.linksheet.experiment.engine.UrlEngineResult
import fe.linksheet.experiment.engine.context.DefaultEngineRunContext
import fe.linksheet.experiment.engine.context.IgnoreLibRedirectExtra
import fe.linksheet.experiment.engine.context.toSourceAppExtra
import fe.linksheet.experiment.engine.fetcher.ContextResultId
import fe.linksheet.experiment.engine.fetcher.preview.toUnfurlResult
import fe.linksheet.experiment.engine.fetcher.toFetchResult
import fe.linksheet.extension.std.toAndroidUri
import fe.linksheet.extension.std.toStdUrl
import fe.linksheet.module.app.PackageService
import fe.linksheet.module.database.dao.base.PackageEntityCreator
import fe.linksheet.module.database.dao.base.WhitelistedBrowsersDao
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.database.entity.whitelisted.WhitelistedBrowser
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.log.Logger
import fe.linksheet.module.network.NetworkStateService
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.CacheRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.BrowserModeConfigHelper
import fe.linksheet.module.resolver.ImprovedBrowserHandler
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.resolver.IntentResolveResult
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.ResolveEvent
import fe.linksheet.module.resolver.ResolveModuleStatus
import fe.linksheet.module.resolver.ResolverInteraction
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.module.resolver.module.BrowserSettings
import fe.linksheet.module.resolver.module.IntentResolverSettings
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlUrlResolver
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectUrlResolver
import fe.linksheet.module.resolver.util.AppSorter
import fe.linksheet.module.resolver.util.CustomTabHandler
import fe.linksheet.module.resolver.util.CustomTabInfo2
import fe.linksheet.module.resolver.util.IntentSanitizer
import fe.linksheet.module.resolver.util.ReferrerHelper
import fe.linksheet.util.intent.parser.IntentParser
import fe.linksheet.util.intent.parser.UriException
import fe.linksheet.util.intent.parser.UriParseException
import fe.std.result.IResult
import fe.std.result.isFailure
import fe.std.result.unaryPlus
import fe.std.uri.StdUrl
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import mozilla.components.support.utils.SafeIntent

class LinkEngineIntentResolver(
    val context: Context,
    val logger: Logger,
    val client: HttpClient,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val normalBrowsersRepository: WhitelistedNormalBrowsersRepository,
    private val inAppBrowsersRepository: WhitelistedInAppBrowsersRepository,
    private val packageService: PackageService,
    private val appSorter: AppSorter,
    private val browserHandler: ImprovedBrowserHandler,
    private val inAppBrowserHandler: InAppBrowserHandler,
    private val networkStateService: NetworkStateService,
    private val selector: TrackSelector,
    private val settings: IntentResolverSettings,
) : IntentResolver {
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
        logger.info(event.toString())
    }

    private fun emitEventIf(predicate: Boolean, event: ResolveEvent) {
        if (!predicate) return
        emitEvent(event)
    }

    private fun emitInteraction(interaction: ResolverInteraction) {
        _interactions.tryEmit(interaction)
        logger.info("Emitted interaction $interaction")
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
            return +uriResult.exception
        }

        val url = uriResult.value.toStdUrl()
        if (url == null) {
            return +UriParseException
        }

        return +url
    }

    override suspend fun resolve(
        intent: SafeIntent,
        referrer: Uri?
    ): IntentResolveResult = coroutineScope scope@{
        val canAccessInternet = networkStateService.isNetworkConnected
        val urlParseResult = parseIntent(intent)
        if (urlParseResult.isFailure()) {
            return@scope IntentResolveResult.IntentParseFailed(urlParseResult.exception as UriException)
        }

        val startUrl = urlParseResult.value
        val referringPackage = ReferrerHelper.getReferringPackage(referrer)
        emitEvent(ResolveEvent.QueryingBrowsers)
        val browsers = packageService.findHttpBrowsable(null)

        val browserPackageMap = browsers?.associateBy { it.activityInfo.packageName } ?: emptyMap()

        val ignoreLibRedirect = shouldIgnoreLibRedirect(intent, libRedirectSettings.enableIgnoreLibRedirectButton())
        val context = DefaultEngineRunContext {
            if (ignoreLibRedirect) {
                add(IgnoreLibRedirectExtra)
            }

            referringPackage?.toSourceAppExtra()?.let(::add)
        }

        val input = Input(startUrl, referringPackage)
        val track = selector.find(input)
        if (track == null) {
            // TODO: What do we do in this situation?
            return@scope IntentResolveResult.NoTrackFound
        }

        val (sealedContext, result) = track.run(startUrl, context)
        if (result !is UrlEngineResult) {
            TODO("Handle this")
        }

        val resultUrl = result.url
        val resultUri = resultUrl.toAndroidUri()

        val downloadResult = sealedContext[ContextResultId.Download]

        val allowCustomTab = inAppBrowserHandler.shouldAllowCustomTab(referrer, browserSettings.inAppBrowserSettings())
        val customTab = CustomTabHandler.getInfo2(intent, allowCustomTab)
        val newIntent = IntentSanitizer.sanitize(intent, Intent.ACTION_VIEW, resultUri, customTab.dropExtras)

        emitEvent(ResolveEvent.LoadingPreferredApps)
        val app = queryPreferredApp(
            repository = preferredAppRepository,
            packageInfoService = packageService,
            uri = resultUri
        )

        val lastUsedApps = queryAppSelectionHistory(
            repository = appSelectionHistoryRepository,
            packageInfoService = packageService,
            uri = resultUri
        )
        val resolveList = packageService.findHandlers(resultUri, referringPackage?.packageName)

        emitEvent(ResolveEvent.CheckingBrowsers)
        val browserModeConfigHelper = createBrowserModeConfig(browserSettings, customTab is CustomTabInfo2.Allowed)
        val appList = browserHandler.filterBrowsers(
            config = browserModeConfigHelper,
            browsers = browserPackageMap,
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
            newIntent,
            resultUri,
            sealedContext[ContextResultId.Preview]?.toUnfurlResult(),
            referrer,
            sorted,
            filtered,
            app?.alwaysPreferred,
            appList.isSingleOption || appList.noBrowsersOnlySingleApp,
            ResolveModuleStatus(),
            sealedContext[ContextResultId.LibRedirect]?.wrapped,
            downloadResult?.toFetchResult()
        )
    }

    private suspend fun createBrowserModeConfig(
        browserSettings: BrowserSettings,
        customTab: Boolean,
    ): BrowserModeConfigHelper {
        if (!browserSettings.unifiedPreferredBrowser() && customTab) {
            return mapToBrowserConfig(
                browserSettings.inAppBrowserMode(),
                browserSettings.selectedInAppBrowser(),
                inAppBrowsersRepository
            )
        }

        return mapToBrowserConfig(
            browserSettings.browserMode(),
            browserSettings.selectedBrowser(),
            normalBrowsersRepository
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

    private fun shouldIgnoreLibRedirect(intent: SafeIntent, enableIgnoreLibRedirectButton: Boolean): Boolean {
        if (!intent.getBooleanExtra(LibRedirectDefault.IgnoreIntentKey, false)) return false
        intent.extras?.remove(LibRedirectDefault.IgnoreIntentKey)
        return enableIgnoreLibRedirectButton
    }

    private suspend fun queryAppSelectionHistory(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        repository: AppSelectionHistoryRepository,
        packageInfoService: PackageService,
        uri: Uri?,
    ): Map<String, Long> = withContext(dispatcher) {
        val lastUsedApps = repository.getLastUsedForHostGroupedByPackage(uri)
            ?: return@withContext emptyMap()

        val (result, delete) = packageInfoService.hasLauncher(lastUsedApps.keys)
        if (delete.isNotEmpty()) repository.delete(delete)

        lastUsedApps.filter { it.key in result }.toMap()
    }

    private suspend fun queryPreferredApp(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        repository: PreferredAppRepository,
        packageInfoService: PackageService,
        uri: Uri?,
    ): PreferredApp? = withContext(dispatcher) {
        val app = repository.getByHost(uri)
        val resolveInfo = packageInfoService.getLauncherOrNull(app?.pkg)
        if (app != null && resolveInfo == null) repository.delete(app)

        app
    }

    override suspend fun warmup() {
    }
}
