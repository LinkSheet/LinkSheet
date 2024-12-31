package fe.linksheet.module.resolver

import android.app.SearchManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.core.content.getSystemService
import fe.clearurlskt.ClearUrls
import fe.clearurlskt.loader.BundledClearURLConfigLoader
import fe.embed.resolve.EmbedResolver
import fe.embed.resolve.config.ConfigType
import fe.fastforwardkt.FastForward
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.extension.android.labelSorted
import fe.linksheet.extension.android.queryResolveInfosByIntent
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.lib.flavors.LinkSheetApp.Compat
import fe.linksheet.module.app.PackageInfoService
import fe.linksheet.module.app.PackageUriHandler
import fe.linksheet.module.database.dao.base.PackageEntityCreator
import fe.linksheet.module.database.dao.base.WhitelistedBrowsersDao
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.database.entity.whitelisted.WhitelistedBrowser
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.intent.IntentParser
import fe.linksheet.module.intent.cloneIntent
import fe.linksheet.module.network.NetworkStateService
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlUrlResolver
import fe.linksheet.module.resolver.urlresolver.base.ResolvePredicate
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectUrlResolver
import fe.linksheet.module.resolver.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import me.saket.unfurl.UnfurlResult
import me.saket.unfurl.Unfurler
import mozilla.components.support.utils.SafeIntent
import org.koin.core.component.KoinComponent

@Stable
class ImprovedIntentResolver(
    val context: Context,
    prefRepo: AppPreferenceRepository,
    featureFlagRepository: FeatureFlagRepository,
    experimentRepository: ExperimentRepository,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val normalBrowsersRepository: WhitelistedNormalBrowsersRepository,
    private val inAppBrowsersRepository: WhitelistedInAppBrowsersRepository,
    private val packageInfoService: PackageInfoService,
    private val downloader: Downloader,
    private val redirectUrlResolver: RedirectUrlResolver,
    private val amp2HtmlResolver: Amp2HtmlUrlResolver,
    private val browserResolver: BrowserResolver,
    private val browserHandler: ImprovedBrowserHandler,
    private val inAppBrowserHandler: InAppBrowserHandler,
    private val libRedirectResolver: LibRedirectResolver,
    private val unfurler: Unfurler,
    private val networkStateService: NetworkStateService,
) : KoinComponent {
    private val logger by injectLogger<ImprovedIntentResolver>()

    private val useClearUrls = prefRepo.asState(AppPreferences.useClearUrls)
    private var useFastForwardRules = prefRepo.asState(AppPreferences.useFastForwardRules)

    private var enableIgnoreLibRedirectButton = prefRepo.asState(AppPreferences.enableIgnoreLibRedirectButton)
    private var enableLibRedirect = prefRepo.asState(AppPreferences.enableLibRedirect)
    private val followRedirects = prefRepo.asState(AppPreferences.followRedirects)
    private val followRedirectsSkipBrowser = prefRepo.asState(AppPreferences.followRedirectsSkipBrowser)

    private val followOnlyKnownTrackers = prefRepo.asState(AppPreferences.followOnlyKnownTrackers)
    private val followRedirectsLocalCache = prefRepo.asState(AppPreferences.followRedirectsLocalCache)

    private val followRedirectsExternalService = prefRepo.asState(AppPreferences.followRedirectsExternalService)

    private val followRedirectsAllowDarknets = prefRepo.asState(AppPreferences.followRedirectsAllowDarknets)
    private val requestTimeout = prefRepo.asState(AppPreferences.requestTimeout)

    private var enableDownloader = prefRepo.asState(AppPreferences.enableDownloader)
    private var downloaderCheckUrlMimeType = prefRepo.asState(AppPreferences.downloaderCheckUrlMimeType)

    private val dontShowFilteredItem = prefRepo.asState(AppPreferences.dontShowFilteredItem)

    private val inAppBrowserSettings = prefRepo.asState(AppPreferences.inAppBrowserSettings)
    private val browserMode = prefRepo.asState(AppPreferences.browserMode)

    @OptIn(SensitivePreference::class)
    private val selectedBrowser = prefRepo.asState(AppPreferences.selectedBrowser)
    private val inAppBrowserMode = prefRepo.asState(AppPreferences.inAppBrowserMode)

    @OptIn(SensitivePreference::class)
    private val selectedInAppBrowser = prefRepo.asState(AppPreferences.selectedInAppBrowser)

    private val unifiedPreferredBrowser = prefRepo.asState(AppPreferences.unifiedPreferredBrowser)

    private val enableAmp2Html = prefRepo.asState(AppPreferences.enableAmp2Html)
    private val amp2HtmlLocalCache = prefRepo.asState(AppPreferences.amp2HtmlLocalCache)


    private val amp2HtmlExternalService = prefRepo.asState(AppPreferences.amp2HtmlExternalService)
    private val amp2HtmlAllowDarknets = prefRepo.asState(AppPreferences.amp2HtmlAllowDarknets)
    private val amp2HtmlSkipBrowser = prefRepo.asState(AppPreferences.amp2HtmlSkipBrowser)

    private val resolveEmbeds = prefRepo.asState(AppPreferences.resolveEmbeds)

    private val previewUrl = experimentRepository.asState(Experiments.urlPreview)
    private val previewUrlSkipBrowser = experimentRepository.asState(Experiments.urlPreviewSkipBrowser)
    private val libRedirectJsEngine = experimentRepository.asState(Experiments.libRedirectJsEngine)
    private val hideReferrerFromSheet = experimentRepository.asState(Experiments.hideReferrerFromSheet)
    private val autoLaunchSingleBrowser = experimentRepository.asState(Experiments.autoLaunchSingleBrowser)

    private val packageUriHandler by lazy {
        PackageUriHandler(
            queryIntentActivities = packageInfoService.queryIntentActivities,
            isLinkSheetCompat = { pkg -> Compat.isApp(pkg) != null },
            checkReferrerExperiment = { hideReferrerFromSheet() }
        )
    }

    private val usageStatsManager by lazy { context.getSystemService<UsageStatsManager>()!! }

    private val appSorter by lazy {
        AppSorter(
            queryAndAggregateUsageStats = usageStatsManager::queryAndAggregateUsageStats,
            toDisplayActivityInfo = packageInfoService::createDisplayActivityInfo
        )
    }

    private val _events = MutableStateFlow(value = ResolveEvent.Initialized)
    val events = _events.asStateFlow()

    private val _interactions = MutableStateFlow<ResolverInteraction>(value = ResolverInteraction.Clear)
    val interactions = _interactions.asStateFlow()

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

//    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
//
//    }

    suspend fun resolve(intent: SafeIntent, referrer: Uri?): IntentResolveResult = coroutineScope scope@{
        initState(ResolveEvent.Initialized, ResolverInteraction.Clear)
        val canAccessInternet = networkStateService.isNetworkConnected

        logger.debug("Referrer=$referrer")
        val referringPackage = ReferrerHelper.getReferringPackage(referrer)
        val isReferrerBrowser = KnownBrowser.isKnownBrowser(referringPackage) != null

        val searchIntentResult = tryHandleSearchIntent(intent)
        if (searchIntentResult != null) return@scope searchIntentResult

        var uri = getUriFromIntent(intent)

        if (uri == null) {
            logger.error("Failed to parse intent ${intent.action}")
            return@scope IntentResolveResult.IntentParseFailed
        }

        emitEvent(ResolveEvent.QueryingBrowsers)
        val browsers = browserResolver.queryBrowsers()

        val resolveEmbeds = resolveEmbeds()
        val useClearUrls = useClearUrls()
        val useFastForwardRules = useFastForwardRules()

        val uriModifiers = resolveEmbeds || useClearUrls || useFastForwardRules

        emitEventIf(uriModifiers, ResolveEvent.ApplyingLinkModifiers)

        uri = runUriModifiers(
            uri = uri,
            resolveEmbeds = resolveEmbeds,
            clearUrl = useClearUrls,
            fastForward = useFastForwardRules
        )

        if (uri == null) {
            return@scope fail("Failed to run uri modifiers", IntentResolveResult.UrlModificationFailed)
        }

        val resolveStatus = ResolveModuleStatus()

        val manualResolveRedirects = intent.getBooleanExtra(IntentKeyResolveRedirects, false)

        val followRedirects = followRedirects() || manualResolveRedirects
        val skipFollowRedirects = followRedirectsSkipBrowser() && isReferrerBrowser

        var resolvedUri: Uri? = uri
        if (followRedirects && !skipFollowRedirects) {
            emitEvent(ResolveEvent.ResolvingRedirects)

            resolvedUri = runRedirectResolver(
                resolveModuleStatus = resolveStatus,
                redirectResolver = redirectUrlResolver,
                uri = uri,
                canAccessInternet = canAccessInternet,
                requestTimeout = requestTimeout(),
                followRedirects = true,
                followRedirectsExternalService = followRedirectsExternalService(),
                followOnlyKnownTrackers = followOnlyKnownTrackers(),
                followRedirectsLocalCache = followRedirectsLocalCache(),
                followRedirectsAllowDarknets = followRedirectsAllowDarknets(),
            )
        }

        val amp2Html = enableAmp2Html()
        val skipAmp2Html = amp2HtmlSkipBrowser() && isReferrerBrowser
        if (amp2Html && !skipAmp2Html) {
            emitEvent(ResolveEvent.RunningAmp2Html)

            resolvedUri = runAmp2HtmlResolver(
                resolveModuleStatus = resolveStatus,
                amp2HtmlResolver = amp2HtmlResolver,
                uri = resolvedUri,
                canAccessInternet = canAccessInternet,
                requestTimeout = requestTimeout(),
                enableAmp2Html = true,
                amp2HtmlAllowDarknets = amp2HtmlAllowDarknets(),
                amp2HtmlExternalService = amp2HtmlExternalService(),
                amp2HtmlLocalCache = amp2HtmlLocalCache()
            )
        }

        if (resolvedUri == null) {
            return@scope fail("Failed to run resolvers", IntentResolveResult.ResolveUrlFailed)
        }

        emitEventIf(uriModifiers, ResolveEvent.ApplyingLinkModifiers)

        uri = runUriModifiers(
            uri = resolvedUri,
            resolveEmbeds = resolveEmbeds,
            clearUrl = useClearUrls,
            fastForward = useFastForwardRules
        )

        if (uri == null) {
            return@scope fail("Failed to run uri modifiers", IntentResolveResult.UrlModificationFailed)
        }

        val enableLibRedirect = enableLibRedirect()
        emitEventIf(enableLibRedirect, ResolveEvent.CheckingLibRedirect)

        val libRedirectResult = tryRunLibRedirect(
            resolver = libRedirectResolver,
            enabled = enableLibRedirect,
            intent = intent,
            uri = uri,
            ignoreLibRedirectButton = enableIgnoreLibRedirectButton(),
            jsEngine = libRedirectJsEngine()
        )

        if (libRedirectResult is LibRedirectResult.Redirected) {
            uri = libRedirectResult.redirectedUri
        }

        val enabledDownloader = enableDownloader()
        emitEventIf(enabledDownloader, ResolveEvent.CheckingDownloader)

        val downloadable = checkDownloadable(
            downloader = downloader,
            enabled = enabledDownloader,
            uri = uri,
            checkUrlMimeType = downloaderCheckUrlMimeType(),
            requestTimeout = requestTimeout()
        )

        val allowCustomTab = inAppBrowserHandler.shouldAllowCustomTab(referrer, inAppBrowserSettings())
        val (customTab, dropExtras) = CustomTabHandler.getInfo(intent, allowCustomTab)
        val newIntent = IntentHandler.sanitize(intent, Intent.ACTION_VIEW, uri, dropExtras)

        emitEvent(ResolveEvent.LoadingPreferredApps)
        val app = queryPreferredApp(
            repository = preferredAppRepository,
            packageInfoService = packageInfoService,
            uri = uri
        )
        val lastUsedApps = queryAppSelectionHistory(
            repository = appSelectionHistoryRepository,
            packageInfoService = packageInfoService, uri = uri
        )
        val resolveList = packageUriHandler.findHandlers(uri, referringPackage)

        emitEvent(ResolveEvent.CheckingBrowsers)
        val browserModeConfigHelper = createBrowserModeConfig(unifiedPreferredBrowser(), customTab)
        val appList = browserHandler.filterBrowsers(browserModeConfigHelper, browsers, resolveList, autoLaunchSingleBrowser())

        emitEvent(ResolveEvent.SortingApps)
        val (sorted, filtered) = appSorter.sort(
            appList,
            app,
            lastUsedApps,
            returnLastChosen = !dontShowFilteredItem()
        )

        val previewUrl = previewUrl()
        var unfurl: UnfurlResult? = null
        val shouldSkipPreviewUrl = previewUrlSkipBrowser() && isReferrerBrowser
        if (previewUrl && !shouldSkipPreviewUrl) {
            emitEvent(ResolveEvent.GeneratingPreview)

            val unfurlDeferred = async { tryUnfurl(uri = uri) }

            val unfurlCancel = ResolverInteraction.Cancelable(ResolveEvent.GeneratingPreview) {
                logger.debug("Cancelling $unfurlDeferred")
                unfurlDeferred.cancel()
//                unfurler.cancel()
            }

            emitInteraction(unfurlCancel)

            Log.d("ImprovedIntentResolver", "Awaiting..")

            try {
                unfurl = unfurlDeferred.await()
            } catch (e: CancellationException) {
                currentCoroutineContext().ensureActive()
                logger.error(e)
            }

            clearInteraction()
        }

        return@scope IntentResolveResult.Default(
            newIntent,
            uri,
            unfurl,
            referrer,
            sorted,
            filtered,
            app?.alwaysPreferred,
            appList.isSingleOption || appList.noBrowsersOnlySingleApp,
            resolveStatus,
            libRedirectResult,
            downloadable
        )
    }

    private suspend fun queryAppSelectionHistory(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        repository: AppSelectionHistoryRepository,
        packageInfoService: PackageInfoService,
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
        packageInfoService: PackageInfoService,
        uri: Uri?,
    ): PreferredApp? = withContext(dispatcher) {
        val app = repository.getByHost(uri)
        val resolveInfo = packageInfoService.getLauncherOrNull(app?.pkg)
        if (app != null && resolveInfo == null) repository.delete(app)

        app
    }

    private fun tryHandleSearchIntent(intent: SafeIntent): IntentResolveResult.WebSearch? {
        if (intent.action != Intent.ACTION_WEB_SEARCH) return null
        val query = IntentParser.parseSearchIntent(intent) ?: return null
        // TODO: Do we need to handle this case? Or is it impossible anyway
//        val uri = IntentParser.tryParse(query) ?: return null
        val newIntent = intent.unsafe
            .cloneIntent(Intent.ACTION_WEB_SEARCH, null, true)
            .putExtra(SearchManager.QUERY, query)

        val resolvedList = context.packageManager
            .queryResolveInfosByIntent(newIntent, true)
            .map { packageInfoService.createDisplayActivityInfo(it, true) }
            .labelSorted()

        return IntentResolveResult.WebSearch(query, newIntent, resolvedList)
    }

    private fun getUriFromIntent(intent: SafeIntent): Uri? {
        if (intent.action == Intent.ACTION_SEND) {
            return IntentParser.parseSendAction(intent)
        }

        if (intent.action == Intent.ACTION_VIEW) {
            return IntentParser.parseViewAction(intent)
        }

        return null
    }

    private suspend fun createBrowserModeConfig(
        unifiedPreferredBrowser: Boolean,
        customTab: Boolean,
    ): BrowserModeConfigHelper {
        if (!unifiedPreferredBrowser && customTab) {
            return mapToBrowserConfig(inAppBrowserMode(), selectedInAppBrowser(), inAppBrowsersRepository)
        }

        return mapToBrowserConfig(browserMode(), selectedBrowser(), normalBrowsersRepository)
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

    private suspend fun tryUnfurl(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
//        enabled: Boolean,
        uri: Uri,
    ): UnfurlResult? = withContext(dispatcher) {
//        if (!enabled) return@withContext null
//        delay(10_000)
        // TODO: Move everything to okhttp
        unfurler.unfurl(uri.toString())
    }

    private suspend fun checkDownloadable(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        downloader: Downloader,
        enabled: Boolean,
        uri: Uri,
        checkUrlMimeType: Boolean,
        requestTimeout: Int,
    ): DownloadCheckResult = withContext(dispatcher) {
        if (!enabled) return@withContext DownloadCheckResult.NonDownloadable

        if (checkUrlMimeType) {
            val result = downloader.checkIsNonHtmlFileEnding(uri.toString())
            if (result.isDownloadable()) return@withContext result
        }

        downloader.isNonHtmlContentUri(uri.toString(), requestTimeout)
    }

    private suspend fun tryRunLibRedirect(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        resolver: LibRedirectResolver,
        enabled: Boolean,
        intent: SafeIntent,
        uri: Uri,
        ignoreLibRedirectButton: Boolean,
        jsEngine: Boolean,
    ): LibRedirectResult? = withContext(dispatcher) {
        if (!enabled) return@withContext null

        val ignoreLibRedirectExtra = intent.getBooleanExtra(LibRedirectDefault.libRedirectIgnore, false)
        if (ignoreLibRedirectExtra) {
            intent.extras?.remove(LibRedirectDefault.libRedirectIgnore)
        }

        if (ignoreLibRedirectExtra && ignoreLibRedirectButton) return@withContext null

        return@withContext resolver.resolve(uri, jsEngine)
    }

    companion object {
        // TODO: Is this a good idea? Do we leak memory? (=> also check libredirect settings)
        private val clearUrlProviders by lazy { BundledClearURLConfigLoader.load().getOrNull() }
        private val embedResolverBundled by lazy { ConfigType.Bundled.load() }

        const val IntentKeyResolveRedirects = "resolve_redirects"
    }

    private val clearUrls = clearUrlProviders?.let { ClearUrls(it) }

    private fun runUriModifiers(
        uri: Uri?,
        resolveEmbeds: Boolean,
        clearUrl: Boolean,
        fastForward: Boolean,
    ): Uri? {
        if (uri?.host == null || uri.scheme == null) return null
        var url = uri.toString()

        runUriModifier(resolveEmbeds) { EmbedResolver.resolve(url, embedResolverBundled) }?.let { url = it }
        runUriModifier(fastForward) { FastForward.getRuleRedirect(url) }?.let { url = it }

        if(clearUrl && clearUrls == null) {
            logger.error("ClearURLs is enabled, but rules failed to load, ignoring..")
        }
        runUriModifier(clearUrl) { clearUrls?.clearUrl(url) }?.let { url = it.first }

        return runCatching { Uri.parse(url) }.getOrNull()
    }

    private inline fun <R> runUriModifier(condition: Boolean, block: () -> R): R? {
        if (!condition) return null
        return runCatching(block).onFailure { logger.error("Uri modification failed", it) }.getOrNull()
    }

    private suspend fun runRedirectResolver(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        resolveModuleStatus: ResolveModuleStatus,
        redirectResolver: RedirectUrlResolver,
        uri: Uri,
        canAccessInternet: Boolean = true,
        requestTimeout: Int,
        followRedirects: Boolean,
        followRedirectsExternalService: Boolean,
        followOnlyKnownTrackers: Boolean,
        followRedirectsLocalCache: Boolean,
        followRedirectsAllowDarknets: Boolean,
    ): Uri? = withContext(dispatcher) {
        logger.debug("Executing runRedirectResolver on ${Thread.currentThread().name}")

        resolveModuleStatus.resolveIfEnabled(followRedirects, ResolveModule.Redirect, uri) { uriToResolve ->
            logger.debug("Inside redirect func, on ${Thread.currentThread().name}")

            val resolvePredicate: ResolvePredicate = { uri ->
                (!followRedirectsExternalService && !followOnlyKnownTrackers) || FastForward.isTracker(uri.toString())
            }

            redirectResolver.resolve(
                uriToResolve,
                followRedirectsLocalCache,
                resolvePredicate,
                followRedirectsExternalService,
                requestTimeout,
                canAccessInternet,
                followRedirectsAllowDarknets
            )
        }
    }

    private suspend fun runAmp2HtmlResolver(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        resolveModuleStatus: ResolveModuleStatus,
        amp2HtmlResolver: Amp2HtmlUrlResolver,
        uri: Uri?,
        canAccessInternet: Boolean = true,
        requestTimeout: Int,
        enableAmp2Html: Boolean,
        amp2HtmlLocalCache: Boolean,
        amp2HtmlExternalService: Boolean,
        amp2HtmlAllowDarknets: Boolean,
    ): Uri? = withContext(dispatcher) {
        logger.debug("Executing runAmp2HtmlResolver on ${Thread.currentThread().name}")

        resolveModuleStatus.resolveIfEnabled(enableAmp2Html, ResolveModule.Amp2Html, uri) { uriToResolve ->
            logger.debug("Inside amp2html func, on ${Thread.currentThread().name}")

            amp2HtmlResolver.resolve(
                uriToResolve,
                amp2HtmlLocalCache,
                null,
                amp2HtmlExternalService,
                requestTimeout,
                canAccessInternet,
                amp2HtmlAllowDarknets,
            )
        }
    }
}
