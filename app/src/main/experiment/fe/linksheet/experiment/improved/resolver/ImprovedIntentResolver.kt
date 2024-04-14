package fe.linksheet.experiment.improved.resolver

import android.app.Application
import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Stable
import fe.clearurlskt.ClearURL
import fe.clearurlskt.ClearURLLoader
import fe.embed.resolve.EmbedResolver
import fe.embed.resolve.config.ConfigType
import fe.fastforwardkt.FastForward
import fe.linksheet.experiment.new.query.manager.query.PackageQueryManager
import fe.linksheet.extension.android.newIntent
import fe.linksheet.extension.android.queryResolveInfosByIntent
import fe.linksheet.extension.android.toDisplayActivityInfos
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.module.database.dao.whitelisted.WhitelistedNormalBrowsersDao
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.database.entity.whitelisted.WhitelistedNormalBrowser
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.*
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlUrlResolver
import fe.linksheet.module.resolver.urlresolver.base.ResolvePredicate
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectUrlResolver
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.IntentParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import me.saket.unfurl.UnfurlResult
import me.saket.unfurl.Unfurler
import mozilla.components.support.utils.SafeIntent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed interface ResolveEvent {
    @JvmInline
    @Stable
    value class Message(val message: String) : ResolveEvent

    companion object{
        val Initialized = Message("Initialized")
    }
}

sealed interface IntentResolveResult {
    data object Pending : IntentResolveResult

    data class WebSearch(
        val query: String, val newIntent: Intent, val resolvedList: List<DisplayActivityInfo>
    ) : IntentResolveResult

    @Stable
    class Default(
        intent: Intent,
        uri: Uri?,
        val unfurlResult: UnfurlResult?,
        referrer: Uri?,
        resolved: List<DisplayActivityInfo>,
        val filteredItem: DisplayActivityInfo?,
        alwaysPreferred: Boolean?,
        hasSingleMatchingOption: Boolean = false,
        val resolveModuleStatus: ResolveModuleStatus,
        val libRedirectResult: LibRedirectResult? = null,
        val downloadable: DownloadCheckResult = DownloadCheckResult.NonDownloadable,
    ) : IntentResolveResult, BottomSheetResult.SuccessResult(uri, intent, resolved) {
        private val totalCount = resolved.size + if (filteredItem != null) 1 else 0

        private val referringPackageName =
            if (referrer?.scheme == "android-app") referrer.host else null

        val isRegularPreferredApp = alwaysPreferred == true && filteredItem != null
        val app = filteredItem ?: resolved[0]

        val hasAutoLaunchApp =
            (isRegularPreferredApp || hasSingleMatchingOption) && (referringPackageName == null || app.packageName != referringPackageName)

        override fun isEmpty(): Boolean {
            return totalCount == 0
        }
    }

    data object IntentParseFailed : IntentResolveResult
    data object UrlModificationFailed : IntentResolveResult
    data object ResolveUrlFailed : IntentResolveResult
}


@Stable
class ImprovedIntentResolver(
    // TODO: DI?
    val context: Application,
    val scope: CoroutineScope,
) : KoinComponent {
    private val logger by injectLogger<ImprovedIntentResolver>()


    // TODO: Refactor to proper DI
    private val redirectUrlResolver by inject<RedirectUrlResolver>()
    private val amp2HtmlResolver by inject<Amp2HtmlUrlResolver>()
    private val libRedirectResolver by inject<LibRedirectResolver>()
    private val downloader by inject<Downloader>()
    private val inAppBrowserHandler by inject<InAppBrowserHandler>()
    private val preferredAppRepository by inject<PreferredAppRepository>()
    private val appSelectionHistoryRepository by inject<AppSelectionHistoryRepository>()

    private val normalBrowsersRepository by inject<WhitelistedNormalBrowsersRepository>()
    private val inAppBrowsersRepository by inject<WhitelistedInAppBrowsersRepository>()

    private val preferenceRepository by inject<AppPreferenceRepository>()

    private val browserResolver = BrowserResolver(context)
    private val browserHandler = BrowserHandler(preferenceRepository, browserResolver)

    private val _events = MutableStateFlow<ResolveEvent>(value = ResolveEvent.Initialized)
    val events = _events.asStateFlow()

    private fun emitEvent(message: String) {
        _events.tryEmit(ResolveEvent.Message(message))
        logger.info(message)
    }

    private fun fail(error: String, result: IntentResolveResult): IntentResolveResult {
        logger.error(error)
        return result
    }

    suspend fun resolve(intent: SafeIntent, referrer: Uri?): IntentResolveResult {
        val searchIntentResult = tryHandleSearchIntent(intent)
        if (searchIntentResult != null) return searchIntentResult

        var uri = getUriFromIntent(intent)

        if (uri == null) {
            Log.d("ImprovedIntentResolver", "Failed to parse intent ${intent.action}")
            return IntentResolveResult.IntentParseFailed
        }

        emitEvent("Querying browser list")
        val browsers = browserResolver.queryBrowsers()

        uri = runUriModifiers(uri = uri)
        if (uri == null) {
            return fail("Failed to run uri modifiers", IntentResolveResult.UrlModificationFailed)
        }

        val (resolveStatus, resolvedUri) = runResolvers(
            uri = uri,
            redirectResolver = redirectUrlResolver,
            amp2HtmlResolver = amp2HtmlResolver
        )

        if (resolvedUri == null) {
            return fail("Failed to run resolvers", IntentResolveResult.ResolveUrlFailed)
        }

        uri = resolvedUri

        uri = runUriModifiers(uri = uri)
        if (uri == null) {
            return fail("Failed to run uri modifiers", IntentResolveResult.UrlModificationFailed)
        }

        val libRedirectResult = tryRunLibRedirect(intent = intent, uri = uri)
        if (libRedirectResult is LibRedirectResult.Redirected) {
            uri = libRedirectResult.redirectedUri
        }

        val downloadable = checkDownloadable(uri = uri)

        // TODO: Should be loaded from prefs
        val inAppBrowserMode = InAppBrowserHandler.InAppBrowserMode.UseAppSettings
        val unifiedPreferredBrowser = true

        val allowCustomTab = inAppBrowserHandler.shouldAllowCustomTab(referrer, inAppBrowserMode)
        val (customTab, dropExtras) = CustomTabHandler.getInfo(intent, allowCustomTab)
        val newIntent = IntentHandler.sanitized(intent, Intent.ACTION_VIEW, uri, dropExtras)

        emitEvent("Loading preferred apps")
        val app = queryPreferredApp(uri = uri)
        val lastUsedApps = queryAppSelectionHistory(uri = uri)
        val resolveList = queryHandlers(newIntent, uri)

        emitEvent("Checking for browsers")
        val browserModeConfigHelper = createBrowserModeConfig(unifiedPreferredBrowser && customTab)
        val appList = browserHandler.filterBrowsers(browserModeConfigHelper, browsers, resolveList)

        emitEvent("Sorting app list")
        val (sorted, filtered) = AppSorter.sort(
            context,
            appList,
            app,
            lastUsedApps,
            returnLastChosen = true
        )

        val unfurl = tryUnfurl(uri = uri)

        return IntentResolveResult.Default(
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
        uri: Uri?,
    ): Map<String, Long> = withContext(dispatcher) {
        val lastUsedApps = appSelectionHistoryRepository.getLastUsedForHostGroupedByPackage(uri)
            ?: return@withContext emptyMap()

        val (result, delete) = PackageInstallHelper.hasLauncher(context, lastUsedApps.keys)
        if (delete.isNotEmpty()) appSelectionHistoryRepository.delete(delete)

        lastUsedApps.filter { it.key in result }.toMap()
    }

    private suspend fun queryPreferredApp(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        uri: Uri?,
    ): PreferredApp? = withContext(dispatcher) {
        val app = preferredAppRepository.getByHost(uri)
        val resolveInfo = PackageInstallHelper.getLauncherOrNull(context, app?.pkg)
        if (app != null && resolveInfo == null) preferredAppRepository.delete(app)

        app
    }

    private fun tryHandleSearchIntent(intent: SafeIntent): IntentResolveResult.WebSearch? {
        if (intent.action != Intent.ACTION_WEB_SEARCH) return null
        val query = IntentParser.parseSearchIntent(intent) ?: return null
        // TODO: Are do we need to handle this case? Is is it impossible anyway
//        val uri = IntentParser.tryParse(query) ?: return null
        val newIntent = intent.unsafe
            .newIntent(Intent.ACTION_WEB_SEARCH, null, true)
            .putExtra(SearchManager.QUERY, query)

        val resolvedList = context.packageManager
            .queryResolveInfosByIntent(newIntent, true)
            .toDisplayActivityInfos(context, true)

        return IntentResolveResult.WebSearch(query, newIntent, resolvedList)
    }

    private fun getUriFromIntent(
        intent: SafeIntent, allowCustomExtras: Boolean = false,
        tryParseAllExtras: Boolean = false,
        parseText: Boolean = false,
    ): Uri? {
        if (intent.action == Intent.ACTION_SEND) {
            return IntentParser.parseSendAction(
                intent,
                allowCustomExtras = allowCustomExtras,
                tryParseAllExtras = tryParseAllExtras,
                parseText = parseText
            )
        }

        if (intent.action == Intent.ACTION_VIEW) {
            return IntentParser.parseViewAction(intent)
        }

        return null
    }

    private fun createBrowserModeConfig(unifiedPreferredBrowser: Boolean = true): BrowserHandler.BrowserModeConfigHelper<WhitelistedNormalBrowser, WhitelistedNormalBrowser.Creator, WhitelistedNormalBrowsersDao> {
        if (unifiedPreferredBrowser) {
            BrowserHandler.BrowserModeConfigHelper(
                browserMode = BrowserHandler.BrowserMode.AlwaysAsk,
                selectedBrowser = null,
                repository = inAppBrowsersRepository
            )
        }

        return BrowserHandler.BrowserModeConfigHelper(
            browserMode = BrowserHandler.BrowserMode.AlwaysAsk,
            selectedBrowser = null,
            repository = normalBrowsersRepository
        )
    }

    private fun queryHandlers(newIntent: Intent, uri: Uri, newQueryManager: Boolean = true): List<UriViewActivity> {
        return if (newQueryManager && AndroidVersion.AT_LEAST_API_31_S) {
            PackageQueryManager.findHandlers(context, uri)
        } else {
            context.packageManager.queryResolveInfosByIntent(newIntent, true).map {
                UriViewActivity(it, false)
            }
        }
    }

    private suspend fun tryUnfurl(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        previewUrl: Boolean = true,
        uri: Uri,
    ): UnfurlResult? = withContext(dispatcher) {
        if (!previewUrl) return@withContext null

        emitEvent("Generating preview")
        // TODO: Move everything to okhttp
        unfurler.unfurl(uri.toString())
    }

    private suspend fun checkDownloadable(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        enableDownloader: Boolean = true,
        uri: Uri,
        downloaderCheckUrlMimeType: Boolean = false,
        requestTimeout: Int = 15,
    ): DownloadCheckResult = withContext(dispatcher) {
        if (!enableDownloader) return@withContext DownloadCheckResult.NonDownloadable
        emitEvent("Checking download-ability")

        if (downloaderCheckUrlMimeType) {
            val result = downloader.checkIsNonHtmlFileEnding(uri.toString())
            if (result.isDownloadable()) return@withContext result
        }

        downloader.isNonHtmlContentUri(uri.toString(), requestTimeout)
    }

    private suspend fun tryRunLibRedirect(
        enableLibRedirect: Boolean = false,
        intent: SafeIntent,
        uri: Uri,
        enableIgnoreLibRedirectButton: Boolean = false,
    ): LibRedirectResult? {
        if (!enableLibRedirect) return null

        val ignoreLibRedirectExtra = intent.getBooleanExtra(LibRedirectDefault.libRedirectIgnore, false)
        if (!ignoreLibRedirectExtra || enableIgnoreLibRedirectButton) return null

        intent.extras?.remove(LibRedirectDefault.libRedirectIgnore)

        emitEvent("Trying to find FOSS-frontend")
        return libRedirectResolver.resolve(uri)
    }

    companion object {
        // TODO: Is this a good idea? Do we leak memory? (=> also check libredirect settings)
        private val clearUrlProviders by lazy { ClearURLLoader.loadBuiltInClearURLProviders() }
        private val embedResolverBundled by lazy { ConfigType.Bundled.load() }
        private val unfurler by lazy { Unfurler() }
    }

    private suspend fun runUriModifiers(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        uri: Uri?,
        resolveEmbeds: Boolean = false,
        clearUrl: Boolean = false,
        fastForward: Boolean = false,
    ): Uri? = withContext(dispatcher) {
        if (uri?.host == null || uri.scheme == null) return@withContext null
        var url = uri.toString()

        emitEvent("Resolving embeds")
        runUriModifier(resolveEmbeds) { EmbedResolver.resolve(url, embedResolverBundled) }?.let { url = it }

        emitEvent("Applying rules")
        runUriModifier(fastForward) { FastForward.getRuleRedirect(url) }?.let { url = it }

        emitEvent("Clearing tracking parameters")
        runUriModifier(clearUrl) { ClearURL.clearUrl(url, clearUrlProviders) }?.let { url = it }

        runCatching { Uri.parse(url) }.getOrNull()
    }

    private inline fun <R> runUriModifier(condition: Boolean, block: () -> R): R? {
        if (!condition) return null
        return runCatching(block).onFailure { logger.error("Uri modification failed", it) }.getOrNull()
    }

    private suspend fun runResolvers(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        uri: Uri,
        redirectResolver: RedirectUrlResolver,
        amp2HtmlResolver: Amp2HtmlUrlResolver,
        canAccessInternet: Boolean = true,
        followRedirects: Boolean = true,
        followRedirectsExternalService: Boolean = false,
        followOnlyKnownTrackers: Boolean = true,
        followRedirectsLocalCache: Boolean = true,
        followRedirectsAllowDarknets: Boolean = false,
        requestTimeout: Int = 15,
        enableAmp2Html: Boolean = true,
        amp2HtmlLocalCache: Boolean = false,
        amp2HtmlExternalService: Boolean = false,
        amp2HtmlAllowDarknets: Boolean = false,
    ): Pair<ResolveModuleStatus, Uri?> = withContext(dispatcher) {
        logger.debug("Executing runResolvers on ${Thread.currentThread().name}")

        val resolveModuleStatus = ResolveModuleStatus()
        var uriMut: Uri? = uri

        logger.debug("Executing redirect resolver on ${Thread.currentThread().name}")

        uriMut = resolveModuleStatus.resolveIfEnabled(followRedirects, ResolveModule.Redirect, uriMut) { uriToResolve ->
            logger.debug("Inside redirect func, on ${Thread.currentThread().name}")

            val resolvePredicate: ResolvePredicate = { uri ->
                (!followRedirectsExternalService && !followOnlyKnownTrackers) || FastForward.isTracker(uri.toString())
            }

            emitEvent("Resolving redirects")

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

        logger.debug("Executing amp2html on ${Thread.currentThread().name}")
        uriMut = resolveModuleStatus.resolveIfEnabled(enableAmp2Html, ResolveModule.Amp2Html, uriMut) { uriToResolve ->
            logger.debug("Inside amp2html func, on ${Thread.currentThread().name}")

            emitEvent("Un-amping link")

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

        resolveModuleStatus to uriMut
    }
}
