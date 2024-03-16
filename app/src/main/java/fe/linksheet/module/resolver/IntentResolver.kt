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
import fe.fastforwardkt.FastForward
import fe.linksheet.extension.android.componentName
import fe.linksheet.extension.android.newIntent
import fe.linksheet.extension.android.queryResolveInfosByIntent
import fe.linksheet.extension.android.toDisplayActivityInfos
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.preference.*
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlUrlResolver
import fe.linksheet.module.resolver.urlresolver.base.AllRemoteResolveRequest
import fe.linksheet.module.resolver.urlresolver.base.ResolvePredicate
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectUrlResolver
import fe.linksheet.resolver.BottomSheetGrouper
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.util.IntentParser
import fe.linksheet.util.UriUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.saket.unfurl.Unfurler
import mozilla.components.support.utils.SafeIntent
import org.koin.core.component.KoinComponent

class IntentResolver(
    val context: Context,
    val preferenceRepository: AppPreferenceRepository,
    val featureFlagRepository: FeatureFlagRepository,
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
    private val libRedirectResolver: LibRedirectResolver,
) : KoinComponent {
    private val logger by injectLogger<IntentResolver>()

    private val useClearUrls = preferenceRepository.asState(AppPreferences.useClearUrls)
    private var useFastForwardRules = preferenceRepository.asState(
        AppPreferences.useFastForwardRules
    )

    private var enableIgnoreLibRedirectButton =
        preferenceRepository.asState(AppPreferences.enableIgnoreLibRedirectButton)
    private var enableLibRedirect =
        preferenceRepository.asState(AppPreferences.enableLibRedirect)
    private val followRedirects = preferenceRepository.asState(AppPreferences.followRedirects)

    private val followOnlyKnownTrackers =
        preferenceRepository.asState(AppPreferences.followOnlyKnownTrackers)
    private val followRedirectsLocalCache = preferenceRepository.asState(
        AppPreferences.followRedirectsLocalCache
    )
    private val followRedirectsBuiltInCache = preferenceRepository.asState(
        AppPreferences.followRedirectsBuiltInCache
    )
    private val followRedirectsExternalService = preferenceRepository.asState(
        AppPreferences.followRedirectsExternalService
    )

    private val followRedirectsAllowDarknets =
        preferenceRepository.asState(AppPreferences.followRedirectsAllowDarknets)

    private val requestTimeout = preferenceRepository.asState(
        AppPreferences.requestTimeout
    )

    private var enableDownloader =
        preferenceRepository.asState(AppPreferences.enableDownloader)
    private var downloaderCheckUrlMimeType = preferenceRepository.asState(
        AppPreferences.downloaderCheckUrlMimeType
    )

    val theme = preferenceRepository.asState(AppPreferences.theme)
    private val dontShowFilteredItem = preferenceRepository.asState(
        AppPreferences.dontShowFilteredItem
    )

    private val inAppBrowserSettings =
        preferenceRepository.asState(AppPreferences.inAppBrowserSettings)

    private val browserMode = preferenceRepository.asState(AppPreferences.browserMode)

    @OptIn(SensitivePreference::class)
    private val selectedBrowser = preferenceRepository.asState(AppPreferences.selectedBrowser)
    private val inAppBrowserMode = preferenceRepository.asState(AppPreferences.inAppBrowserMode)

    @OptIn(SensitivePreference::class)
    private val selectedInAppBrowser =
        preferenceRepository.asState(AppPreferences.selectedInAppBrowser)

    private val unifiedPreferredBrowser =
        preferenceRepository.asState(AppPreferences.unifiedPreferredBrowser)

    private val enableAmp2Html = preferenceRepository.asState(AppPreferences.enableAmp2Html)
    private val amp2HtmlLocalCache = preferenceRepository.asState(
        AppPreferences.amp2HtmlLocalCache
    )

    private val amp2HtmlBuiltInCache = preferenceRepository.asState(
        AppPreferences.amp2HtmlBuiltInCache
    )

    private val amp2HtmlExternalService =
        preferenceRepository.asState(AppPreferences.amp2HtmlExternalService)
    private val amp2HtmlAllowDarknets =
        preferenceRepository.asState(AppPreferences.amp2HtmlAllowDarknets)

    private val resolveEmbeds = preferenceRepository.asState(AppPreferences.resolveEmbeds)

    private val previewUrl = featureFlagRepository.asState(FeatureFlags.urlPreview)
    private val parseShareText = featureFlagRepository.asState(FeatureFlags.parseShareText)
    private val allowCustomShareExtras = featureFlagRepository.asState(FeatureFlags.allowCustomShareExtras)
    private val checkAllExtras = featureFlagRepository.asState(FeatureFlags.checkAllExtras)

    companion object {
        private val clearUrlProviders by lazy { ClearURLLoader.loadBuiltInClearURLProviders() }
        private val embedResolverBundled by lazy { ConfigType.Bundled.load() }
        private val unfurler by lazy { Unfurler() }
    }

    suspend fun resolveIfEnabled(intent: SafeIntent, referrer: Uri?, canAccessInternet: Boolean): BottomSheetResult {
//        logger.debug({ "Intent=$it"}, intent, NoOpProcessor)
//        val x = intent
        urlResolverCache.clear()

        var startUri: Uri? = null
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

                    return BottomSheetResult.BottomSheetWebSearchResult(
                        query,
                        newIntent,
                        resolvedList
                    )
                } else {
                    startUri = uri
                }
            }
        }

        val ignoreLibRedirectExtra = intent.getBooleanExtra(
            LibRedirectDefault.libRedirectIgnore, false
        )

        if (ignoreLibRedirectExtra) {
            intent.extras?.remove(LibRedirectDefault.libRedirectIgnore)
        }

        if (intent.action == Intent.ACTION_SEND) {
            startUri = IntentParser.parseSendAction(
                intent,
                allowCustomExtras = allowCustomShareExtras(),
                tryParseAllExtras = checkAllExtras(),
                parseText = parseShareText()
            )
        } else if (intent.action == Intent.ACTION_VIEW) {
            startUri = IntentParser.parseViewAction(intent)
        }

        if (startUri == null) {
            logger.error("Failed to parse intent ${intent.action}")
            return BottomSheetResult.BottomSheetNoHandlersFound()
        }

        var uri = modifyUri(startUri, resolveEmbeds(), useClearUrls(), useFastForwardRules())

        if (uri == null) {
            logger.error("Uri is null, something probably went very wrong")
            return BottomSheetResult.BottomSheetNoHandlersFound()
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

        val lastUsedApps = withContext(Dispatchers.IO) {
            appSelectionHistoryRepository.getLastUsedForHostGroupedByPackage(uri)
        }

        val isCustomTab = intent.hasExtra(CustomTabsIntent.EXTRA_SESSION)
        val allowCustomTab = inAppBrowserHandler.shouldAllowCustomTab(
            referrer, inAppBrowserSettings()
        )

        val newIntent = intent.unsafe.newIntent(Intent.ACTION_VIEW, uri, !isCustomTab || !allowCustomTab)
        if (allowCustomTab) {
            newIntent.extras?.keySet()?.filter { !it.contains("customtabs") }?.forEach { key ->
//                Timber.tag("ResolveIntents").d("CustomTab: Remove extra: $key")
                newIntent.removeExtra(key)
            }
        }

        val resolvedList: MutableList<ResolveInfo> = context.packageManager
            .queryResolveInfosByIntent(newIntent, true)
            .toMutableList()

        logger.debug(resolvedList, HashProcessor.ResolveInfoListProcessor, { it }, "ResolveList")

        if (resolvedList.isEmpty()) {
            return BottomSheetResult.BottomSheetNoHandlersFound(uri)
        }

        val browserMode = if (UriUtil.hasWebScheme(newIntent)) {
            val (
                mode,
                selected,
                repository,
            ) = if (!unifiedPreferredBrowser() && isCustomTab && allowCustomTab) {

                Triple(inAppBrowserMode, selectedInAppBrowser, inAppBrowsersRepository)
            } else Triple(browserMode, selectedBrowser, normalBrowsersRepository)

            browserHandler.handleBrowsers(mode(), selected(), repository, resolvedList)
        } else null


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

        logger.debug("BrowserMode: $browserMode, IsSingleOption: $selectedBrowserIsSingleOption, OnlySingleApp: $noBrowsersPresentOnlySingleApp")

        logger.debug(grouped, HashProcessor.DisplayActivityInfoListProcessor, { it }, "Grouped")
        logger.debug(filteredItem, HashProcessor.DisplayActivityInfoProcessor, { it }, "FilteredItem")

        val unfurlResult = if (uri != null && previewUrl()) {
            unfurler.unfurl(uri.toString())
        } else null

        return BottomSheetResult.BottomSheetSuccessResult(
            newIntent,
            uri,
            unfurlResult,
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

            logger.debug(url, HashProcessor.UrlProcessor, { "Input: $it" }, "ModifyUri")

            if (resolveEmbeds) {
                runCatching {
                    EmbedResolver.resolve(url, embedResolverBundled)?.let { url = it }
                }.onFailure { logger.error(throwable = it, subPrefix = "EmbedResolver") }
            }

            logger.debug(url, HashProcessor.UrlProcessor, { "Output: $it" }, "EmbedResolver")

            if (fastForward) {
                runCatching {
                    FastForward.getRuleRedirect(url)?.let { url = it }
                }.onFailure { logger.error(throwable = it, subPrefix = "FastForward") }
            }

            logger.debug(url, HashProcessor.UrlProcessor, { "Output: $it" }, "FastForward")

            if (clearUrl) {
                runCatching {
                    url = ClearURL.clearUrl(url, clearUrlProviders)
                }.onFailure { logger.error(throwable = it, subPrefix = "ClearUrls") }
            }

            logger.debug(url, HashProcessor.UrlProcessor, { "Output: $it" }, "ClearURLs")
            return runCatching { Uri.parse(url) }.getOrNull()
        }

        return null
    }
}
