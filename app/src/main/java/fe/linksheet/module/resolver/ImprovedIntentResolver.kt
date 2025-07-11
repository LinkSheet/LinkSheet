package fe.linksheet.module.resolver

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Stable
import fe.clearurlskt.ClearUrls
import fe.clearurlskt.loader.BundledClearURLConfigLoader
import fe.embed.resolve.EmbedResolver
import fe.embed.resolve.loader.BundledEmbedResolveConfigLoader
import fe.fastforwardkt.FastForward
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.extension.std.toStdUrl
import fe.linksheet.feature.app.PackageService
import fe.linksheet.feature.app.labelSorted
import fe.linksheet.module.database.dao.base.PackageEntityCreator
import fe.linksheet.module.database.dao.base.WhitelistedBrowsersDao
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.database.entity.cache.ResolveType
import fe.linksheet.module.database.entity.whitelisted.WhitelistedBrowser
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.log.Logger
import fe.linksheet.module.network.NetworkStateService
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.module.resolver.module.BrowserSettings
import fe.linksheet.module.resolver.module.IntentResolverSettings
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlUrlResolver
import fe.linksheet.module.resolver.urlresolver.base.ResolvePredicate
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectUrlResolver
import fe.linksheet.module.resolver.util.AppSorter
import fe.linksheet.module.resolver.util.CustomTabHandler
import fe.linksheet.module.resolver.util.IntentSanitizer
import fe.linksheet.util.AndroidUriHelper
import fe.linksheet.util.intent.parser.IntentParser
import fe.linksheet.util.intent.cloneIntent
import fe.linksheet.util.intent.parser.UriException
import fe.std.result.getOrNull
import fe.std.result.isFailure
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import me.saket.unfurl.UnfurlResult
import me.saket.unfurl.Unfurler
import mozilla.components.support.utils.SafeIntent
import kotlin.lazy

@Stable
class ImprovedIntentResolver(
    val context: Context,
    val logger: Logger,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val normalBrowsersRepository: WhitelistedNormalBrowsersRepository,
    private val inAppBrowsersRepository: WhitelistedInAppBrowsersRepository,
    private val packageInfoService: PackageService,
    private val appSorter: AppSorter,
    private val downloader: Downloader,
    private val redirectUrlResolver: RedirectUrlResolver,
    private val amp2HtmlResolver: Amp2HtmlUrlResolver,
    private val browserHandler: ImprovedBrowserHandler,
    private val inAppBrowserHandler: InAppBrowserHandler,
    private val libRedirectResolver: LibRedirectResolver,
    private val unfurler: Unfurler,
    private val networkStateService: NetworkStateService,
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

//    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
//
//    }

    override suspend fun resolve(intent: SafeIntent, referrer: Uri?): IntentResolveResult = coroutineScope scope@{
        initState(ResolveEvent.Initialized, ResolverInteraction.Initialized)
        val canAccessInternet = networkStateService.isNetworkConnected

        logger.debug("Referrer=$referrer")
        val referringPackage = AndroidUriHelper.get(AndroidUriHelper.Type.Package, referrer)
        val isReferrerBrowser = KnownBrowser.isKnownBrowser(referringPackage?.packageName) != null

        val searchIntentResult = tryHandleSearchIntent(intent)
        if (searchIntentResult != null) {
            return@scope searchIntentResult
        }

        val uriResult = IntentParser.getUriFromIntent(intent)

        if (uriResult.isFailure()) {
            logger.error("Failed to parse intent ${intent.action}")
            return@scope IntentResolveResult.IntentParseFailed(uriResult.exception as UriException)
        }

        var uri = uriResult.getOrNull()

        emitEvent(ResolveEvent.QueryingBrowsers)
        val browsers = packageInfoService.findHttpBrowsable(null)

        // TODO: Refactor properly
        val browserPackageMap = browsers?.associateBy { it.activityInfo.packageName } ?: emptyMap()

        val resolveEmbeds = settings.resolveEmbeds()
        val useClearUrls = settings.useClearUrls()
        val useFastForwardRules = settings.useFastForwardRules()

        val shouldRunUriModifiers = resolveEmbeds || useClearUrls || useFastForwardRules
        if (shouldRunUriModifiers) {
            // TODO: We do an "eager" cache warmup in the activity anyway, do we still need this? Should we add a toggle for eager cache warmup?
            warmup()
        }

        emitEventIf(shouldRunUriModifiers, ResolveEvent.ApplyingLinkModifiers)

        uri = this@ImprovedIntentResolver.runUriModifiers(
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

        val followRedirects = followRedirectsSettings.followRedirects() || manualResolveRedirects
        val skipFollowRedirects = followRedirectsSettings.followRedirectsSkipBrowser() && isReferrerBrowser

        var resolvedUri: Uri? = uri
        if (followRedirects && !skipFollowRedirects) {
            emitEvent(ResolveEvent.ResolvingRedirects)

            resolvedUri = runRedirectResolver(
                resolveModuleStatus = resolveStatus,
                redirectResolver = redirectUrlResolver,
                uri = uri,
                canAccessInternet = canAccessInternet,
                requestTimeout = settings.requestTimeout(),
                followRedirects = true,
                followRedirectsExternalService = followRedirectsSettings.followRedirectsExternalService(),
                followOnlyKnownTrackers = followRedirectsSettings.followOnlyKnownTrackers(),
                followRedirectsLocalCache = followRedirectsSettings.followRedirectsLocalCache(),
                followRedirectsAllowDarknets = followRedirectsSettings.followRedirectsAllowDarknets(),
                followRedirectsAllowLocalNetwork = followRedirectsSettings.followRedirectsAllowLocalNetwork()
            )
        }

        val amp2Html = amp2HtmlSettings.enableAmp2Html()
        val skipAmp2Html = amp2HtmlSettings.amp2HtmlSkipBrowser() && isReferrerBrowser
        if (amp2Html && !skipAmp2Html) {
            emitEvent(ResolveEvent.RunningAmp2Html)

            resolvedUri = runAmp2HtmlResolver(
                resolveModuleStatus = resolveStatus,
                amp2HtmlResolver = amp2HtmlResolver,
                uri = resolvedUri,
                canAccessInternet = canAccessInternet,
                requestTimeout = settings.requestTimeout(),
                enableAmp2Html = true,
                amp2HtmlAllowDarknets = amp2HtmlSettings.amp2HtmlAllowDarknets(),
                amp2HtmlAllowLocalNetwork = amp2HtmlSettings.amp2HtmlAllowLocalNetwork(),
                amp2HtmlExternalService = amp2HtmlSettings.amp2HtmlExternalService(),
                amp2HtmlLocalCache = amp2HtmlSettings.amp2HtmlLocalCache()
            )
        }

        if (resolvedUri == null) {
            return@scope fail("Failed to run resolvers", IntentResolveResult.ResolveUrlFailed)
        }

        emitEventIf(shouldRunUriModifiers, ResolveEvent.ApplyingLinkModifiers)

        uri = this@ImprovedIntentResolver.runUriModifiers(
            uri = resolvedUri,
            resolveEmbeds = resolveEmbeds,
            clearUrl = useClearUrls,
            fastForward = useFastForwardRules
        )

        if (uri == null) {
            return@scope fail("Failed to run uri modifiers", IntentResolveResult.UrlModificationFailed)
        }

        val enableLibRedirect = libRedirectSettings.enableLibRedirect()
        emitEventIf(enableLibRedirect, ResolveEvent.CheckingLibRedirect)

        val libRedirectResult = tryRunLibRedirect(
            resolver = libRedirectResolver,
            enabled = enableLibRedirect,
            intent = intent,
            uri = uri,
            ignoreLibRedirectButton = libRedirectSettings.enableIgnoreLibRedirectButton(),
            jsEngine = libRedirectSettings.libRedirectJsEngine()
        )

        if (libRedirectResult is LibRedirectResult.Redirected) {
            uri = libRedirectResult.redirectedUri
        }

        val enabledDownloader = downloaderSettings.enableDownloader()
        emitEventIf(enabledDownloader, ResolveEvent.CheckingDownloader)

        val downloadable = checkDownloadable(
            downloader = downloader,
            enabled = enabledDownloader,
            uri = uri,
            checkUrlMimeType = downloaderSettings.downloaderCheckUrlMimeType(),
            requestTimeout = settings.requestTimeout()
        )

        val allowCustomTab = inAppBrowserHandler.shouldAllowCustomTab(referrer, browserSettings.inAppBrowserSettings())
        val (customTab, dropExtras) = CustomTabHandler.getInfo(intent, allowCustomTab)
        val newIntent = IntentSanitizer.sanitize(intent, Intent.ACTION_VIEW, uri, dropExtras)

        emitEvent(ResolveEvent.LoadingPreferredApps)
        val app = queryPreferredApp(
            repository = preferredAppRepository,
            packageInfoService = packageInfoService,
            uri = uri
        )
        val lastUsedApps = queryAppSelectionHistory(
            repository = appSelectionHistoryRepository,
            packageInfoService = packageInfoService,
            uri = uri
        )
        val resolveList = packageInfoService.findHandlers(uri, referringPackage?.packageName)

        emitEvent(ResolveEvent.CheckingBrowsers)
        val browserModeConfigHelper = createBrowserModeConfig(browserSettings, customTab)
        val appList = browserHandler.filterBrowsers(browserModeConfigHelper, browserPackageMap, resolveList)

        emitEvent(ResolveEvent.SortingApps)
        val (sorted, filtered) = appSorter.sort(
            appList,
            app,
            lastUsedApps,
            returnLastChosen = !settings.dontShowFilteredItem()
        )

        val previewUrl = previewSettings.previewUrl()
        var unfurl: UnfurlResult? = null
        val shouldSkipPreviewUrl = previewSettings.previewUrlSkipBrowser() && isReferrerBrowser
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
            referringPackage?.packageName,
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

    private fun tryHandleSearchIntent(intent: SafeIntent): IntentResolveResult.WebSearch? {
        if (intent.action != Intent.ACTION_WEB_SEARCH) return null
        val query = IntentParser.parseSearchIntent(intent) ?: return null
        val newIntent = intent.unsafe
            .cloneIntent(Intent.ACTION_WEB_SEARCH, null, true)
            .putExtra(SearchManager.QUERY, query)

        val resolvedList = packageInfoService.findHandlers(newIntent)
            .map { packageInfoService.toAppInfo(it, true) }
            .labelSorted()

        return IntentResolveResult.WebSearch(query, newIntent, resolvedList)
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
        uri: Uri?,
        checkUrlMimeType: Boolean,
        requestTimeout: Int,
    ): DownloadCheckResult = withContext(dispatcher) {
        if (!enabled) return@withContext DownloadCheckResult.NonDownloadable
        val url = uri?.toStdUrl() ?: return@withContext DownloadCheckResult.NonDownloadable

        if (checkUrlMimeType) {
            val result = downloader.checkIsNonHtmlFileEnding(url)
            if (result.isDownloadable()) return@withContext result
        }

        downloader.isNonHtmlContentUri(url, requestTimeout)
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

        val ignoreLibRedirectExtra = intent.getBooleanExtra(LibRedirectDefault.IgnoreIntentKey, false)
        if (ignoreLibRedirectExtra) {
            intent.extras?.remove(LibRedirectDefault.IgnoreIntentKey)
        }

        if (ignoreLibRedirectExtra && ignoreLibRedirectButton) return@withContext null

        return@withContext resolver.resolve(uri, jsEngine)
    }

    fun <T> CoroutineScope.suspendLazy(initializer: suspend CoroutineScope.() -> T): Deferred<T> {
        return async(start = CoroutineStart.LAZY, block = initializer)
    }

    companion object {
        // TODO: Is this a good idea? Do we leak memory? (=> also check libredirect settings)
        private val clearUrlProviders by lazy { BundledClearURLConfigLoader.load().getOrNull() }
        private val embedResolverConfig by lazy { BundledEmbedResolveConfigLoader.load().getOrNull() }

        const val IntentKeyResolveRedirects = "resolve_redirects"
    }

    private val clearUrls by lazy { clearUrlProviders?.let { ClearUrls(it) } }
    private val embedResolver by lazy { embedResolverConfig?.let { EmbedResolver(it) } }

    override suspend fun warmup(): Unit = withContext(Dispatchers.IO) {
        clearUrlProviders
        embedResolverConfig
        libRedirectResolver.warmup()
    }

    private fun runUriModifiers(
        uri: Uri?,
        resolveEmbeds: Boolean,
        clearUrl: Boolean,
        fastForward: Boolean,
    ): Uri? {
        if (uri?.host == null || uri.scheme == null) return null
        var url = uri.toString()

        if (resolveEmbeds && embedResolver == null) {
            logger.error("embed-resolve is enabled, but config failed to load, ignoring..")
        }

        runUriModifier(resolveEmbeds) { embedResolver?.resolve(url) }?.let { url = it }

        runUriModifier(fastForward) { FastForward.getRuleRedirect(url) }?.let { url = it }

        if (clearUrl && clearUrls == null) {
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
        followRedirectsAllowLocalNetwork: Boolean
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
                followRedirectsAllowDarknets,
                followRedirectsAllowLocalNetwork,
                ResolveType.FollowRedirects
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
        amp2HtmlAllowLocalNetwork: Boolean
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
                amp2HtmlAllowLocalNetwork,
                ResolveType.Amp2Html
            )
        }
    }
}
