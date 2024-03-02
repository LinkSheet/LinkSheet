package fe.linksheet.module.resolver

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import fe.clearurlskt.ClearURL
import fe.clearurlskt.ClearURLLoader
import fe.embed.resolve.EmbedResolver
import fe.embed.resolve.config.ConfigType
import fe.linksheet.extension.android.IntentExt.getUri
import fe.linksheet.extension.android.componentName
import fe.linksheet.extension.android.newIntent
import fe.linksheet.extension.android.queryResolveInfosByIntent
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.log.impl.hasher.HashProcessor
import fe.linksheet.module.log.factory.LoggerFactory
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlUrlResolver
import fe.linksheet.module.resolver.urlresolver.base.AllRemoteResolveRequest
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectUrlResolver
import fe.linksheet.resolver.BottomSheetGrouper
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.util.UriUtil
import fe.fastforwardkt.*
import fe.linksheet.extension.android.toDisplayActivityInfos
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.resolver.urlresolver.base.ResolvePredicate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IntentResolver(
    val context: Context,
    loggerFactory: LoggerFactory,
    val preferenceRepository: AppPreferenceRepository,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val normalBrowsersRepository: WhitelistedNormalBrowsersRepository,
    private val inAppBrowsersRepository: WhitelistedInAppBrowsersRepository,
    private val urlResolverCache: CachedRequest,
    private val downloader: Downloader,
    private val redirectResolver: RedirectUrlResolver,
    private val amp2HtmlResolver: Amp2HtmlUrlResolver,
    private val allRemoteResolveRequest: AllRemoteResolveRequest,
    private val browserHandler: BrowserHandler,
    private val inAppBrowserHandler: InAppBrowserHandler,
    private val libRedirectResolver: LibRedirectResolver
) {
    private val logger = loggerFactory.createLogger(IntentResolver::class)

    private val useClearUrls = preferenceRepository.getBooleanState(AppPreferences.useClearUrls)
    private var useFastForwardRules = preferenceRepository.getBooleanState(
        AppPreferences.useFastForwardRules
    )

    private var enableIgnoreLibRedirectButton =
        preferenceRepository.getBooleanState(AppPreferences.enableIgnoreLibRedirectButton)
    private var enableLibRedirect =
        preferenceRepository.getBooleanState(AppPreferences.enableLibRedirect)
    private val followRedirects = preferenceRepository.getBooleanState(AppPreferences.followRedirects)

    private val followOnlyKnownTrackers =
        preferenceRepository.getBooleanState(AppPreferences.followOnlyKnownTrackers)
    private val followRedirectsLocalCache = preferenceRepository.getBooleanState(
        AppPreferences.followRedirectsLocalCache
    )
    private val followRedirectsBuiltInCache = preferenceRepository.getBooleanState(
        AppPreferences.followRedirectsBuiltInCache
    )
    private val followRedirectsExternalService = preferenceRepository.getBooleanState(
        AppPreferences.followRedirectsExternalService
    )

    private val followRedirectsAllowDarknets =
        preferenceRepository.getBooleanState(AppPreferences.followRedirectsAllowDarknets)

    private val requestTimeout = preferenceRepository.getIntState(
        AppPreferences.requestTimeout
    )

    private var enableDownloader =
        preferenceRepository.getBooleanState(AppPreferences.enableDownloader)
    private var downloaderCheckUrlMimeType = preferenceRepository.getBooleanState(
        AppPreferences.downloaderCheckUrlMimeType
    )

    val theme = preferenceRepository.getState(AppPreferences.theme)
    private val dontShowFilteredItem = preferenceRepository.getBooleanState(
        AppPreferences.dontShowFilteredItem
    )

    private val inAppBrowserSettings =
        preferenceRepository.getState(AppPreferences.inAppBrowserSettings)

    private val browserMode = preferenceRepository.getState(AppPreferences.browserMode)
    @OptIn(SensitivePreference::class)
    private val selectedBrowser = preferenceRepository.getStringState(AppPreferences.selectedBrowser)
    private val inAppBrowserMode = preferenceRepository.getState(AppPreferences.inAppBrowserMode)
    @OptIn(SensitivePreference::class)
    private val selectedInAppBrowser =
        preferenceRepository.getStringState(AppPreferences.selectedInAppBrowser)

    private val unifiedPreferredBrowser =
        preferenceRepository.getBooleanState(AppPreferences.unifiedPreferredBrowser)

    private val enableAmp2Html = preferenceRepository.getBooleanState(AppPreferences.enableAmp2Html)
    private val amp2HtmlLocalCache = preferenceRepository.getBooleanState(
        AppPreferences.amp2HtmlLocalCache
    )

    private val amp2HtmlBuiltInCache = preferenceRepository.getBooleanState(
        AppPreferences.amp2HtmlBuiltInCache
    )

    private val amp2HtmlExternalService =
        preferenceRepository.getBooleanState(AppPreferences.amp2HtmlExternalService)
    private val amp2HtmlAllowDarknets =
        preferenceRepository.getBooleanState(AppPreferences.amp2HtmlAllowDarknets)

    private val resolveEmbeds = preferenceRepository.getBooleanState(AppPreferences.resolveEmbeds)


    companion object {
        private val clearUrlProviders = ClearURLLoader.loadBuiltInClearURLProviders()
    }


    suspend fun resolveIfEnabled(intent: Intent, referrer: Uri?, canAccessInternet: Boolean): BottomSheetResult {
//        logger.debug({ "Intent=$it"}, intent, NoOpProcessor)
//        val x = intent
        urlResolverCache.clear()

        if (intent.action == Intent.ACTION_WEB_SEARCH) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            if (query != null) {
                val newIntent = intent
                    .newIntent(Intent.ACTION_WEB_SEARCH, null, true)
                    .putExtra(SearchManager.QUERY, query)

                val resolvedList = context.packageManager
                    .queryResolveInfosByIntent(newIntent, true)
                    .toDisplayActivityInfos(context, true)

                return BottomSheetResult.BottomSheetWebSearchResult(
                    query,
                    newIntent,
                    resolvedList
                )
            }
        }

        val ignoreLibRedirectExtra = intent.getBooleanExtra(
            LibRedirectDefault.libRedirectIgnore, false
        )

        if (ignoreLibRedirectExtra) {
            intent.extras?.remove(LibRedirectDefault.libRedirectIgnore)
        }

        var uri = modifyUri(intent.getUri(), resolveEmbeds(), useClearUrls(), useFastForwardRules())

        if (uri == null) {
            logger.error("Uri is null, something probably went very wrong")
        }

//        var followRedirectsResult: Result<ResolveType>
        val resolveModuleStatus = ResolveModuleStatus()
//
//        if (followRedirects() && followRedirectsExternalService() && enableAmp2Html() && amp2HtmlExternalService()) {
//            val uriString = uri.toString()
//            val con = allRemoteResolveRequest.resolveRemote(uriString, requestTimeout())
//            if (!isHttpSuccess(con.responseCode)) {
//                logger.debug("Failed to resolve via external service (${con.responseCode}): ${con.readToString()}")
//            }
//
//            val obj = con.readToJson().asJsonObject
//            val url = obj.getAsJsonPrimitive("result").asString
//
//            val results = obj.getAsJsonObject("results")
//            Resolved()s().forEach {
//
//
//                val prim = results.getAsJsonPrimitive(it.key)
//
//
//                resolved[it] = Result.success(
//                    if (prim != null) ResolveType.NotResolved(url) else ResolveType.Remote(url)
//                )
//            }
//        } else {
        uri = resolveModuleStatus.resolveIfEnabled(followRedirects(), ResolveModule.Redirect, uri) { uriToResolve ->
            val externalService = followRedirectsExternalService()

            val resolvePredicate: ResolvePredicate = { uri ->
                (!externalService && !followOnlyKnownTrackers()) || FastForward.isTracker(uri.toString())
            }

            redirectResolver.resolve(
                uriToResolve,
                followRedirectsLocalCache(),
                followRedirectsBuiltInCache(),
                resolvePredicate,
                externalService,
                requestTimeout(),
                canAccessInternet,
                followRedirectsAllowDarknets()
            )
        }

        uri = resolveModuleStatus.resolveIfEnabled(enableAmp2Html(), ResolveModule.Amp2Html, uri) { uriToResolve ->
            amp2HtmlResolver.resolve(
                uriToResolve,
                amp2HtmlLocalCache(),
                amp2HtmlBuiltInCache(),
                null,
                amp2HtmlExternalService(),
                requestTimeout(),
                canAccessInternet,
                amp2HtmlAllowDarknets(),
            )
        }

        uri = modifyUri(uri, resolveEmbeds(), useClearUrls(), useFastForwardRules())

        var libRedirectResult: LibRedirectResolver.LibRedirectResult? = null
        if (enableLibRedirect() && uri != null && !(ignoreLibRedirectExtra && enableIgnoreLibRedirectButton())) {
            libRedirectResult = libRedirectResolver.resolve(uri)
            if (libRedirectResult is LibRedirectResolver.LibRedirectResult.Redirected) {
                uri = libRedirectResult.redirectedUri
            }
        }

        val downloadable = if (enableDownloader() && uri != null) {
            checkIsDownloadable(uri, requestTimeout())
        } else Downloader.DownloadCheckResult.NonDownloadable

        val preferredApp = preferredAppRepository.getByHost(uri)
        val preferredDisplayActivityInfo = preferredApp?.toPreferredDisplayActivityInfo(context)
        if (preferredApp != null && preferredDisplayActivityInfo == null) {
            withContext(Dispatchers.IO) {
                preferredAppRepository.deleteByPackageName(preferredApp.packageName!!)
            }
        }

//        logger.debug({"PreferredApp=$it"}, preferredApp, HashProcessor)

        val lastUsedApps = appSelectionHistoryRepository.getLastUsedForHostGroupedByPackage(uri)

//        logger.debug(
//            {"LastUsedApps=$it"},
//            lastUsedApps?.toDumpable(
//                "packageName", "lastUsed",
//                { it },
//                { it.toString() },
//                PackageProcessor,
//                HashProcessor.NoOpProcessor
//            )
//        )

        val isCustomTab = intent.hasExtra(CustomTabsIntent.EXTRA_SESSION)
        val allowCustomTab = inAppBrowserHandler.shouldAllowCustomTab(
            referrer, inAppBrowserSettings()
        )

        val newIntent = intent.newIntent(Intent.ACTION_VIEW, uri, !isCustomTab || !allowCustomTab)
        if (allowCustomTab) {
            newIntent.extras?.keySet()?.filter { !it.contains("customtabs") }?.forEach { key ->
//                Timber.tag("ResolveIntents").d("CustomTab: Remove extra: $key")
                newIntent.removeExtra(key)
            }
        }

//        logger.debug("NewIntent=$newIntent")

        val resolvedList: MutableList<ResolveInfo> = context.packageManager
            .queryResolveInfosByIntent(newIntent, true)
            .toMutableList()

        logger.debug({ it }, resolvedList, HashProcessor.ResolveInfoListProcessor, "ResolveList")

        if (resolvedList.isEmpty()) {
            return BottomSheetResult.BottomSheetNoHandlersFound(uri)
        }

        val browserMode = if (UriUtil.hasWebScheme(newIntent)) {
            val (
                mode,
                selected,
                repository
            ) = if (!unifiedPreferredBrowser() && isCustomTab && allowCustomTab) {

                Triple(inAppBrowserMode, selectedInAppBrowser, inAppBrowsersRepository)
            } else Triple(browserMode, selectedBrowser, normalBrowsersRepository)

            browserHandler.handleBrowsers(mode(), selected(), repository, resolvedList)
        } else null

        logger.debug("BrowserMode: $browserMode")

        val (grouped, filteredItem, showExtended) = BottomSheetGrouper.group(
            context,
            resolvedList,
            lastUsedApps,
            preferredDisplayActivityInfo?.app,
            !dontShowFilteredItem()
        )

        val selectedBrowserIsSingleOption =
            browserMode?.browserMode == BrowserHandler.BrowserMode.SelectedBrowser
                    && resolvedList.singleOrNull()?.activityInfo?.componentName() == browserMode.resolveInfo?.activityInfo?.componentName()

        val noBrowsersPresentOnlySingleApp =
            browserMode?.browserMode == BrowserHandler.BrowserMode.None && resolvedList.size == 1

//        logger.debug(
//            "Grouped=%s, filteredItem=%s, showExtended=%s, selectedBrowserIsSingleOption=%s, noBrowsersPresentOnlySingleApp=%s",
//            grouped,
//            filteredItem,
//            showExtended,
//            selectedBrowserIsSingleOption,
//            noBrowsersPresentOnlySingleApp
//        )

//        val host = referrer?.host
//        val scheme = referrer?.scheme
//
//        val linkSheetReferrer = intent.hasExtra(LinkSheetConnector.EXTRA_REFERRER)
//        val intentReferrer = intent.hasExtra(Intent.EXTRA_REFERRER)

        return BottomSheetResult.BottomSheetSuccessResult(
            newIntent,
            uri,
            referrer,
            grouped,
            filteredItem,
            showExtended,
            preferredDisplayActivityInfo?.app?.alwaysPreferred,
            selectedBrowserIsSingleOption || noBrowsersPresentOnlySingleApp,
            resolveModuleStatus,
            libRedirectResult,
            downloadable
        )
    }

    private fun checkIsDownloadable(uri: Uri, timeout: Int): Downloader.DownloadCheckResult {
        if (downloaderCheckUrlMimeType()) {
            downloader.checkIsNonHtmlFileEnding(uri.toString()).let {
                logger.debug("File ending check result=$it")
                if (it.isDownloadable()) return it
            }
        }

        return downloader.isNonHtmlContentUri(uri.toString(), timeout)
    }

    private fun modifyUri(
        uri: Uri?,
        resolveEmbeds: Boolean = false,
        clearUrl: Boolean = false,
        fastForward: Boolean = false,
    ): Uri? {
        if (uri?.host != null && uri.scheme != null) {
            var url = uri.toString()

            logger.debug({ "Input: $it" }, url, HashProcessor.UrlProcessor, "ModifyUri")

            if (resolveEmbeds) {
                runCatching {
                    EmbedResolver.resolve(url, ConfigType.Bundled())?.let { url = it }
                }.onFailure { logger.error(it, "EmbedResolver") }
            }

            logger.debug({ "Output: $it" }, url, HashProcessor.UrlProcessor, "EmbedResolver")

            if (fastForward) {
                runCatching {
                    FastForward.getRuleRedirect(url)?.let { url = it }
                }.onFailure { logger.error(it, "FastForward") }
            }

            logger.debug({ "Output: $it" }, url, HashProcessor.UrlProcessor, "FastForward")

            if (clearUrl) {
                runCatching {
                    url = ClearURL.clearUrl(url, clearUrlProviders)
                }.onFailure { logger.error(it, "ClearUrls") }
            }

            logger.debug({ "Output: $it" }, url, HashProcessor.UrlProcessor, "ClearURLs")
            return runCatching { Uri.parse(url) }.getOrNull()
        }

        return null
    }
}
