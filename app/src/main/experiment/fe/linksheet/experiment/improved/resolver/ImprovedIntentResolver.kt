package fe.linksheet.experiment.improved.resolver

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Stable
import fe.clearurlskt.ClearURL
import fe.clearurlskt.ClearURLLoader
import fe.embed.resolve.EmbedResolver
import fe.embed.resolve.config.ConfigType
import fe.fastforwardkt.FastForward
import fe.linksheet.module.resolver.PackageHandler
import fe.linksheet.extension.android.newIntent
import fe.linksheet.extension.android.queryResolveInfosByIntent
import fe.linksheet.extension.android.toDisplayActivityInfos
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.*
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlUrlResolver
import fe.linksheet.module.resolver.urlresolver.base.ResolvePredicate
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectUrlResolver
import fe.linksheet.util.IntentParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
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
    private val downloader: Downloader,
    private val redirectUrlResolver: RedirectUrlResolver,
    private val amp2HtmlResolver: Amp2HtmlUrlResolver,
    private val browserHandler: BrowserHandler,
    private val inAppBrowserHandler: InAppBrowserHandler,
    private val libRedirectResolver: LibRedirectResolver,
) : KoinComponent {
    private val logger by injectLogger<ImprovedIntentResolver>()

    private val useClearUrls = prefRepo.asState(AppPreferences.useClearUrls)
    private var useFastForwardRules = prefRepo.asState(AppPreferences.useFastForwardRules)

    private var enableIgnoreLibRedirectButton = prefRepo.asState(AppPreferences.enableIgnoreLibRedirectButton)
    private var enableLibRedirect = prefRepo.asState(AppPreferences.enableLibRedirect)
    private val followRedirects = prefRepo.asState(AppPreferences.followRedirects)

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

    private val resolveEmbeds = prefRepo.asState(AppPreferences.resolveEmbeds)

    private val previewUrl = experimentRepository.asState(Experiments.urlPreview)

    private val browserResolver = BrowserResolver(context)

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

        uri = runUriModifiers(
            uri = uri,
            resolveEmbeds = resolveEmbeds(),
            clearUrl = useClearUrls(),
            fastForward = useFastForwardRules()
        )

        if (uri == null) {
            return fail("Failed to run uri modifiers", IntentResolveResult.UrlModificationFailed)
        }

        val (resolveStatus, resolvedUri) = runResolvers(
            uri = uri,
            // TODO: Check internet
            canAccessInternet = true,
            redirectResolver = redirectUrlResolver,
            amp2HtmlResolver = amp2HtmlResolver,
            followRedirects = followRedirects(),
            followRedirectsExternalService = followRedirectsExternalService(),
            followOnlyKnownTrackers = followOnlyKnownTrackers(),
            followRedirectsLocalCache = followRedirectsLocalCache(),
            followRedirectsAllowDarknets = followRedirectsAllowDarknets(),
            requestTimeout = requestTimeout(),
            enableAmp2Html = enableAmp2Html(),
            amp2HtmlAllowDarknets = amp2HtmlAllowDarknets(),
            amp2HtmlExternalService = amp2HtmlExternalService(),
            amp2HtmlLocalCache = amp2HtmlLocalCache()
        )

        if (resolvedUri == null) {
            return fail("Failed to run resolvers", IntentResolveResult.ResolveUrlFailed)
        }

        uri = resolvedUri

        uri = runUriModifiers(
            uri = uri,
            resolveEmbeds = resolveEmbeds(),
            clearUrl = useClearUrls(),
            fastForward = useFastForwardRules()
        )

        if (uri == null) {
            return fail("Failed to run uri modifiers", IntentResolveResult.UrlModificationFailed)
        }

        val libRedirectResult = tryRunLibRedirect(
            enabled = enableLibRedirect(),
            intent = intent,
            uri = uri,
            ignoreLibRedirectButton = enableIgnoreLibRedirectButton()
        )

        if (libRedirectResult is LibRedirectResult.Redirected) {
            uri = libRedirectResult.redirectedUri
        }

        val downloadable = checkDownloadable(
            enabled = enableDownloader(),
            uri = uri,
            checkUrlMimeType = downloaderCheckUrlMimeType(),
            requestTimeout = requestTimeout()
        )

        val allowCustomTab = inAppBrowserHandler.shouldAllowCustomTab(referrer, inAppBrowserSettings())
        val (customTab, dropExtras) = CustomTabHandler.getInfo(intent, allowCustomTab)
        val newIntent = IntentHandler.sanitized(intent, Intent.ACTION_VIEW, uri, dropExtras)

        emitEvent("Loading preferred apps")
        val app = queryPreferredApp(uri = uri)
        val lastUsedApps = queryAppSelectionHistory(uri = uri)
        val resolveList = PackageHandler.findHandlers(context, uri)

        emitEvent("Checking for browsers")
        val browserModeConfigHelper = createBrowserModeConfig(unifiedPreferredBrowser(), customTab)
        val appList = browserHandler.filterBrowsers(browserModeConfigHelper, browsers, resolveList)

        emitEvent("Sorting app list")
        val (sorted, filtered) = AppSorter.sort(
            context,
            appList,
            app,
            lastUsedApps,
            returnLastChosen = !dontShowFilteredItem()
        )

        val unfurl = tryUnfurl(enabled = previewUrl(), uri = uri)

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
        // TODO: Do we need to handle this case? Or is it impossible anyway
//        val uri = IntentParser.tryParse(query) ?: return null
        val newIntent = intent.unsafe
            .newIntent(Intent.ACTION_WEB_SEARCH, null, true)
            .putExtra(SearchManager.QUERY, query)

        val resolvedList = context.packageManager
            .queryResolveInfosByIntent(newIntent, true)
            .toDisplayActivityInfos(context, true)

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

    private fun createBrowserModeConfig(
        unifiedPreferredBrowser: Boolean,
        customTab: Boolean,
    ): BrowserHandler.BrowserModeConfigHelper<*, *, *> {
        if (!unifiedPreferredBrowser && customTab) {
            return BrowserHandler.BrowserModeConfigHelper(
                browserMode = inAppBrowserMode(),
                selectedBrowser = selectedInAppBrowser(),
                repository = inAppBrowsersRepository
            )
        }

        return BrowserHandler.BrowserModeConfigHelper(
            browserMode = browserMode(),
            selectedBrowser = selectedBrowser(),
            repository = normalBrowsersRepository
        )
    }

    private suspend fun tryUnfurl(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        enabled: Boolean,
        uri: Uri,
    ): UnfurlResult? = withContext(dispatcher) {
        if (!enabled) return@withContext null

        emitEvent("Generating preview")
        // TODO: Move everything to okhttp
        unfurler.unfurl(uri.toString())
    }

    private suspend fun checkDownloadable(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        enabled: Boolean,
        uri: Uri,
        checkUrlMimeType: Boolean,
        requestTimeout: Int,
    ): DownloadCheckResult = withContext(dispatcher) {
        if (!enabled) return@withContext DownloadCheckResult.NonDownloadable
        emitEvent("Checking download-ability")

        if (checkUrlMimeType) {
            val result = downloader.checkIsNonHtmlFileEnding(uri.toString())
            if (result.isDownloadable()) return@withContext result
        }

        downloader.isNonHtmlContentUri(uri.toString(), requestTimeout)
    }

    private suspend fun tryRunLibRedirect(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        enabled: Boolean,
        intent: SafeIntent,
        uri: Uri,
        ignoreLibRedirectButton: Boolean,
    ): LibRedirectResult? = withContext(dispatcher) {
        if (!enabled) return@withContext null

        val ignoreLibRedirectExtra = intent.getBooleanExtra(LibRedirectDefault.libRedirectIgnore, false)
        if (ignoreLibRedirectExtra) {
            intent.extras?.remove(LibRedirectDefault.libRedirectIgnore)
        }

        if (ignoreLibRedirectExtra && ignoreLibRedirectButton) return@withContext null

        emitEvent("Trying to find FOSS-frontend")
        return@withContext libRedirectResolver.resolve(uri)
    }

    companion object {
        // TODO: Is this a good idea? Do we leak memory? (=> also check libredirect settings)
        private val clearUrlProviders by lazy { ClearURLLoader.loadBuiltInClearURLProviders() }
        private val embedResolverBundled by lazy { ConfigType.Bundled.load() }
        private val unfurler by lazy { Unfurler() }
    }

    private  fun runUriModifiers(
        uri: Uri?,
        resolveEmbeds: Boolean,
        clearUrl: Boolean,
        fastForward: Boolean,
    ): Uri? {
        if (uri?.host == null || uri.scheme == null) return null
        var url = uri.toString()

        emitEvent("Resolving embeds")
        runUriModifier(resolveEmbeds) { EmbedResolver.resolve(url, embedResolverBundled) }?.let { url = it }

        emitEvent("Applying rules")
        runUriModifier(fastForward) { FastForward.getRuleRedirect(url) }?.let { url = it }

        emitEvent("Clearing tracking parameters")
        runUriModifier(clearUrl) { ClearURL.clearUrl(url, clearUrlProviders) }?.let { url = it }

        return runCatching { Uri.parse(url) }.getOrNull()
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
        followRedirects: Boolean,
        followRedirectsExternalService: Boolean,
        followOnlyKnownTrackers: Boolean,
        followRedirectsLocalCache: Boolean,
        followRedirectsAllowDarknets: Boolean,
        requestTimeout: Int,
        enableAmp2Html: Boolean,
        amp2HtmlLocalCache: Boolean,
        amp2HtmlExternalService: Boolean,
        amp2HtmlAllowDarknets: Boolean,
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
