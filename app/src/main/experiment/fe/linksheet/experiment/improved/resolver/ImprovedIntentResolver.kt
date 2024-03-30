package fe.linksheet.experiment.improved.resolver

import android.app.Application
import android.app.SearchManager
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
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
import fe.linksheet.module.database.dao.whitelisted.WhitelistedNormalBrowsersDao
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.database.entity.whitelisted.WhitelistedNormalBrowser
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import me.saket.unfurl.UnfurlResult
import me.saket.unfurl.Unfurler
import mozilla.components.support.utils.SafeIntent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed interface ResolveEvent {

    //    data class InstalledBrowsers(val count: Int) : ResolveEvent
//
//    data object StartResolvers : ResolveEvent
    @Stable
    open class Message(val message: String) : ResolveEvent

    data object Initialized : Message("Initialized")
}

sealed interface IntentResolveResult {
    data object Pending : IntentResolveResult

    data class WebSearch(val query: String, val newIntent: Intent, val resolvedList: List<DisplayActivityInfo>) :
        IntentResolveResult

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
        val libRedirectResult: LibRedirectResolver.LibRedirectResult? = null,
        val downloadable: Downloader.DownloadCheckResult = Downloader.DownloadCheckResult.NonDownloadable,
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

    suspend fun resolve(intent: SafeIntent, referrer: Uri?): IntentResolveResult {
        if (intent.action == Intent.ACTION_WEB_SEARCH) {
            val query = IntentParser.parseSearchIntent(intent)
            if (query != null) {
                val uri = IntentParser.tryParse(query)
                if (uri == null) {
                    val newIntent = intent.unsafe
                        .newIntent(Intent.ACTION_WEB_SEARCH, null, true)
                        .putExtra(SearchManager.QUERY, query)

                    val resolvedList = context.packageManager
                        .queryResolveInfosByIntent(newIntent, true)
                        .toDisplayActivityInfos(context, true)

                    return IntentResolveResult.WebSearch(query, newIntent, resolvedList)
                } else {
                    Log.d("ImprovedIntentResolver", "TODO: $uri")
//                    startUri = uri
                }
            }
        }

        var uri = getUriFromIntent(intent)

        if (uri == null) {
            Log.d("ImprovedIntentResolver", "Failed to parse intent ${intent.action}")
            return IntentResolveResult.IntentParseFailed
        }

        val browsers = browserResolver.queryBrowsers()
        _events.emit(ResolveEvent.Message("Found ${browsers.size} browsers"))

        uri = runUriModifiers(uri = uri)
        if (uri == null) {
            Log.d("ImprovedIntentResolver", "Failed to run uri modifiers")
            return IntentResolveResult.UrlModificationFailed
        }

        _events.emit(ResolveEvent.Message("Running resolvers on $uri"))

        val (resolveStatus, resolvedUri) = runResolvers(
            uri = uri,
            redirectResolver = redirectUrlResolver,
            amp2HtmlResolver = amp2HtmlResolver
        )

        if (resolvedUri == null) {
            Log.d("ImprovedIntentResolver", "Failed to run resolvers")
            return IntentResolveResult.ResolveUrlFailed
        }

        uri = resolvedUri

        _events.emit(ResolveEvent.Message("Running modifiers on $uri"))
        uri = runUriModifiers(uri = uri)
        if (uri == null) {
            Log.d("ImprovedIntentResolver", "Failed to run uri modifiers")
            return IntentResolveResult.UrlModificationFailed
        }

        val libRedirectResult = tryRunLibRedirect(intent = intent, uri = uri)
        if (libRedirectResult is LibRedirectResolver.LibRedirectResult.Redirected) {
            uri = libRedirectResult.redirectedUri
        }

        _events.emit(ResolveEvent.Message("Checking if $uri is downloadable"))

        val downloadable = checkDownloadable(uri = uri)
        val (newIntent, customTabInfo) = handleCustomTab(
            intent,
            uri,
            inAppBrowserHandler.shouldAllowCustomTab(referrer, InAppBrowserHandler.InAppBrowserMode.UseAppSettings)
        )

        _events.emit(ResolveEvent.Message("Querying stored data for ${uri.host}"))

        val preferredApp = preferredAppRepository.getByHost(uri)
        val preferredDisplayActivityInfo = preferredApp?.toPreferredDisplayActivityInfo(context)
        if (preferredApp != null && preferredDisplayActivityInfo == null) {
            withContext(Dispatchers.IO) {
                preferredAppRepository.deleteByPackageName(preferredApp.pkg!!)
            }
        }

        val lastUsedApps = withContext(Dispatchers.IO) {
            appSelectionHistoryRepository.getLastUsedForHostGroupedByPackage(uri)
        }

        val resolveList = queryHandlers(newIntent, uri)
        val useInAppConfig = customTabInfo.useInAppConfig(unifiedPreferredBrowser = true)

        val browserModeConfigHelper = createConfig(useInAppConfig)
        val appList = browserHandler.filterBrowsers(browserModeConfigHelper, browsers, resolveList)

        val (sorted, filtered) = AppSorter.sort(
            context,
            appList,
            preferredDisplayActivityInfo?.app,
            lastUsedApps,
            returnLastChosen = true
        )

        _events.emit(ResolveEvent.Message("Loading preview for $uri"))
        val unfurl = tryUnfurl(uri = uri)

        return IntentResolveResult.Default(
            newIntent,
            uri,
            unfurl,
            referrer,
            sorted,
            filtered,
            preferredDisplayActivityInfo?.app?.alwaysPreferred,
            appList.isSingleOption || appList.noBrowsersOnlySingleApp,
            resolveStatus,
            libRedirectResult,
            downloadable
        )
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

    private fun createConfig(useInAppConfig: Boolean): BrowserHandler.BrowserModeConfigHelper<WhitelistedNormalBrowser, WhitelistedNormalBrowser.Creator, WhitelistedNormalBrowsersDao> {
        if (useInAppConfig) {
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

    private fun queryHandlers(newIntent: Intent, uri: Uri, newQueryManager: Boolean = true): List<ResolveInfo> {
        return if (newQueryManager && AndroidVersion.AT_LEAST_API_31_S) {
            PackageQueryManager.findHandlers(context, uri)
        } else {
            context.packageManager.queryResolveInfosByIntent(newIntent, true)
        }
    }

    data class CustomTabInfo(
        val isCustomTab: Boolean,
        val allowCustomTab: Boolean,
    ) {
        fun useInAppConfig(unifiedPreferredBrowser: Boolean): Boolean {
            return !unifiedPreferredBrowser && isCustomTab && allowCustomTab
        }
    }

    private fun handleCustomTab(intent: SafeIntent, uri: Uri, allowCustomTab: Boolean): Pair<Intent, CustomTabInfo> {
        val isCustomTab = intent.hasExtra(CustomTabsIntent.EXTRA_SESSION)
//        val allowCustomTab =

        val newIntent = intent.unsafe.newIntent(Intent.ACTION_VIEW, uri, !isCustomTab || !allowCustomTab)
        if (allowCustomTab) {
            newIntent.extras?.keySet()?.filter { !it.contains("customtabs") }?.forEach { key ->
//                Timber.tag("ResolveIntents").d("CustomTab: Remove extra: $key")
                newIntent.removeExtra(key)
            }
        }

        return newIntent to CustomTabInfo(isCustomTab, allowCustomTab)
    }

    private suspend fun tryUnfurl(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        previewUrl: Boolean = true,
        uri: Uri,
    ): UnfurlResult? {
        if (!previewUrl) return null

        // TODO: Move everything to okhttp
        return withContext(dispatcher) {
            unfurler.unfurl(uri.toString())
        }
    }

    private suspend fun checkDownloadable(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        enableDownloader: Boolean = true,
        uri: Uri,
        downloaderCheckUrlMimeType: Boolean = false,
        requestTimeout: Int = 15,
    ): Downloader.DownloadCheckResult = withContext(dispatcher) {
        if (!enableDownloader) return@withContext Downloader.DownloadCheckResult.NonDownloadable

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
    ): LibRedirectResolver.LibRedirectResult? {
        if (enableLibRedirect) {
            val ignoreLibRedirectExtra = intent.getBooleanExtra(LibRedirectDefault.libRedirectIgnore, false)
            if (ignoreLibRedirectExtra && !enableIgnoreLibRedirectButton) {
                intent.extras?.remove(LibRedirectDefault.libRedirectIgnore)

//                val libRedirectResult = libRedirectResolver.resolve(uri)
//                if (libRedirectResult is LibRedirectResolver.LibRedirectResult.Redirected) {
                return libRedirectResolver.resolve(uri)
//                }
            }
        }

        return null
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

        runUriModifier(resolveEmbeds) { EmbedResolver.resolve(url, embedResolverBundled) }?.let { url = it }
        runUriModifier(fastForward) { FastForward.getRuleRedirect(url) }?.let { url = it }
        runUriModifier(clearUrl) { ClearURL.clearUrl(url, clearUrlProviders) }?.let { url = it }

        runCatching { Uri.parse(url) }.getOrNull()
    }

    private inline fun <R> runUriModifier(condition: Boolean, block: () -> R): R? {
        if (!condition) return null

        return runCatching(block).onFailure {
            it.printStackTrace()
            // TODO: Log
        }.getOrNull()
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
        val resolveModuleStatus = ResolveModuleStatus()
        var uriMut: Uri? = uri

        uriMut = resolveModuleStatus.resolveIfEnabled(followRedirects, ResolveModule.Redirect, uriMut) { uriToResolve ->
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

        uriMut = resolveModuleStatus.resolveIfEnabled(enableAmp2Html, ResolveModule.Amp2Html, uriMut) { uriToResolve ->
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
