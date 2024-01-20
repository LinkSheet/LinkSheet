package fe.linksheet.module.resolver

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import fe.android.preference.helper.compose.getBooleanState
import fe.android.preference.helper.compose.getIntState
import fe.android.preference.helper.compose.getState
import fe.android.preference.helper.compose.getStringState
import fe.clearurlskt.ClearURL
import fe.clearurlskt.ClearURLLoader
import fe.linksheet.extension.android.IntentExt.getUri
import fe.linksheet.extension.android.componentName
import fe.linksheet.extension.android.newIntent
import fe.linksheet.extension.android.queryResolveInfosByIntent
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.log.hasher.HashProcessor
import fe.linksheet.module.log.LoggerFactory
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
    private val selectedBrowser = preferenceRepository.getStringState(AppPreferences.selectedBrowser)
    private val inAppBrowserMode = preferenceRepository.getState(AppPreferences.inAppBrowserMode)
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


    companion object {
        private val clearUrlProviders = ClearURLLoader.loadBuiltInClearURLProviders()
    }


    suspend fun resolveIfEnabled(intent: Intent, referrer: Uri?, canAccessInternet: Boolean): BottomSheetResult {
//        logger.debug({ "Intent=$it"}, intent, NoOpProcessor)
//        val x = intent
        urlResolverCache.clear()

        val ignoreLibRedirectExtra = intent.getBooleanExtra(
            LibRedirectDefault.libRedirectIgnore, false
        )

        if (ignoreLibRedirectExtra) {
            intent.extras?.remove(LibRedirectDefault.libRedirectIgnore)
        }

        var uri = modifyUri(intent.getUri(), useClearUrls.value, useFastForwardRules.value)

        if (uri == null) {
            logger.debug("Uri is null, something probably went very wrong")
        }

//        var followRedirectsResult: Result<ResolveType>
        val resolveModuleStatus = ResolveModuleStatus()
//
//        if (followRedirects.value && followRedirectsExternalService.value && enableAmp2Html.value && amp2HtmlExternalService.value) {
//            val uriString = uri.toString()
//            val con = allRemoteResolveRequest.resolveRemote(uriString, requestTimeout.value)
//            if (!isHttpSuccess(con.responseCode)) {
//                logger.debug("Failed to resolve via external service (${con.responseCode}): ${con.readToString()}")
//            }
//
//            val obj = con.readToJson().asJsonObject
//            val url = obj.getAsJsonPrimitive("result").asString
//
//            val results = obj.getAsJsonObject("results")
//            Resolved.values().forEach {
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
        uri = resolveModuleStatus.resolveIfEnabled(followRedirects.value, ResolveModule.Redirect, uri) { uriToResolve ->
            val externalService = followRedirectsExternalService.value

            val resolvePredicate: ResolvePredicate = { uri ->
                (!externalService && !followOnlyKnownTrackers.value) || FastForward.isTracker(uri.toString())
            }

            redirectResolver.resolve(
                uriToResolve,
                followRedirectsLocalCache.value,
                followRedirectsBuiltInCache.value,
                resolvePredicate,
                externalService,
                requestTimeout.value,
                canAccessInternet
            )
        }

        uri = resolveModuleStatus.resolveIfEnabled(enableAmp2Html.value, ResolveModule.Amp2Html, uri) { uriToResolve ->
            amp2HtmlResolver.resolve(
                uriToResolve,
                amp2HtmlLocalCache.value,
                amp2HtmlBuiltInCache.value,
                null,
                amp2HtmlExternalService.value,
                requestTimeout.value,
                canAccessInternet
            )
        }

        uri = modifyUri(uri, useClearUrls.value, useFastForwardRules.value)

        var libRedirectResult: LibRedirectResolver.LibRedirectResult? = null
        if (enableLibRedirect.value && uri != null && !(ignoreLibRedirectExtra && enableIgnoreLibRedirectButton.value)) {
            libRedirectResult = libRedirectResolver.resolve(uri)
            if (libRedirectResult is LibRedirectResolver.LibRedirectResult.Redirected) {
                uri = libRedirectResult.redirectedUri
            }
        }

        val downloadable = if (enableDownloader.value && uri != null) {
            checkIsDownloadable(uri, requestTimeout.value)
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
            referrer, inAppBrowserSettings.value
        )

        val newIntent = intent.newIntent(uri, !isCustomTab || !allowCustomTab)
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
            ) = if (!unifiedPreferredBrowser.value && isCustomTab && allowCustomTab) {

                Triple(inAppBrowserMode, selectedInAppBrowser, inAppBrowsersRepository)
            } else Triple(browserMode, selectedBrowser, normalBrowsersRepository)

            browserHandler.handleBrowsers(mode.value, selected.value, repository, resolvedList)
        } else null

        logger.debug("BrowserMode: $browserMode")

        val (grouped, filteredItem, showExtended) = BottomSheetGrouper.group(
            context,
            resolvedList,
            lastUsedApps,
            preferredDisplayActivityInfo?.app,
            !dontShowFilteredItem.value
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

//    private suspend fun resolveIfEnabled(
//        enabled: Boolean,
//        uri: Uri?,
//        resolve: suspend (Uri) -> Result<ResolveResultType>?
//    ): Pair<Result<ResolveResultType>?, Uri?> {
//        if (enabled && uri != null) {
//            val resolveResult = resolve(uri)
//
//            // resolveResult == null means that no "resolving" occurred (e.g. setting followOnlyTrackers is enabled)
//            return resolveResult to resolveResult?.getOrNull().getUrlOrDefault(uri)
//        }
//
//        return null to uri
//    }

    private fun checkIsDownloadable(uri: Uri, timeout: Int): Downloader.DownloadCheckResult {
        if (downloaderCheckUrlMimeType.value) {
            downloader.checkIsNonHtmlFileEnding(uri.toString()).let {
                logger.debug("File ending check result=$it")
                if (it.isDownloadable()) return it
            }
        }

        return downloader.isNonHtmlContentUri(uri.toString(), timeout)
    }

    private fun modifyUri(
        uri: Uri?,
        clearUrl: Boolean = false,
        fastForward: Boolean = false,
    ): Uri? {
        if (uri?.host != null && uri.scheme != null) {
            var url = uri.toString()

            logger.debug({ "Input: $it" }, url, HashProcessor.UrlProcessor, "ModifyUri")

            runCatching {
                if (fastForward) {
//                    FastForwardRules.

                    FastForward.getRuleRedirect(url)?.let { url = it }
                }
            }.onFailure { logger.debug(it, "FastForward") }

            logger.debug({ "Output: $it" }, url, HashProcessor.UrlProcessor, "FastForward")

            runCatching {
                if (clearUrl) {
                    url = ClearURL.clearUrl(url, clearUrlProviders)
                }
            }.onFailure { logger.debug(it, "ClearUrls") }

            logger.debug({ "Output: $it" }, url, HashProcessor.UrlProcessor, "ClearURLs")
            return runCatching { Uri.parse(url) }.getOrNull()
        }

        return null
    }
}
