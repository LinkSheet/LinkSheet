package fe.linksheet.module.resolver

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.android.preference.helper.compose.getIntState
import fe.android.preference.helper.compose.getState
import fe.android.preference.helper.compose.getStringState
import fe.fastforwardkt.FastForwardLoader
import fe.linksheet.extension.IntentExt.getUri
import fe.linksheet.extension.IntentExt.isSchemeTypicallySupportedByBrowsers
import fe.linksheet.extension.IntentExt.newIntent
import fe.linksheet.extension.componentName
import fe.linksheet.extension.queryResolveInfosByIntent
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.log.HashProcessor
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.log.PackageProcessor
import fe.linksheet.module.log.toDumpable
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.WhitelistedNormalBrowsersRepository
import fe.linksheet.resolver.BottomSheetGrouper
import fe.linksheet.resolver.BottomSheetResult

class IntentResolver(
    val context: Context,
    loggerFactory: LoggerFactory,
    val preferenceRepository: PreferenceRepository,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val normalBrowsersRepository: WhitelistedNormalBrowsersRepository,
    private val inAppBrowsersRepository: WhitelistedInAppBrowsersRepository,
    private val downloader: Downloader,
    private val redirectFollower: RedirectFollower,
    private val browserHandler: BrowserHandler,
    private val inAppBrowserHandler: InAppBrowserHandler,
    private val libRedirectResolver: LibRedirectResolver
) {
    private val logger = loggerFactory.createLogger(IntentResolver::class)

    private val fastForwardRulesObject by lazy { FastForwardLoader.loadBuiltInFastForwardRules() }

    private val useClearUrls = preferenceRepository.getBooleanState(Preferences.useClearUrls)
    private var useFastForwardRules = preferenceRepository.getBooleanState(
        Preferences.useFastForwardRules
    )

    private var enableIgnoreLibRedirectButton =
        preferenceRepository.getBooleanState(Preferences.enableIgnoreLibRedirectButton)
    private var enableLibRedirect =
        preferenceRepository.getBooleanState(Preferences.enableLibRedirect)
    private val followRedirects = preferenceRepository.getBooleanState(Preferences.followRedirects)

    private val followOnlyKnownTrackers =
        preferenceRepository.getBooleanState(Preferences.followOnlyKnownTrackers)
    private val followRedirectsLocalCache = preferenceRepository.getBooleanState(
        Preferences.followRedirectsLocalCache
    )
    private val followRedirectsExternalService = preferenceRepository.getBooleanState(
        Preferences.followRedirectsExternalService
    )

    private val followRedirectsTimeout = preferenceRepository.getIntState(
        Preferences.followRedirectsTimeout
    )

    private var enableDownloader =
        preferenceRepository.getBooleanState(Preferences.enableDownloader)
    private var downloaderCheckUrlMimeType = preferenceRepository.getBooleanState(
        Preferences.downloaderCheckUrlMimeType
    )
    private val downloaderTimeout = preferenceRepository.getIntState(
        Preferences.downloaderTimeout
    )

    val theme = preferenceRepository.getState(Preferences.theme)
    private val dontShowFilteredItem = preferenceRepository.getBooleanState(
        Preferences.dontShowFilteredItem
    )

    private val inAppBrowserSettings =
        preferenceRepository.getState(Preferences.inAppBrowserSettings)

    private val browserMode = preferenceRepository.getState(Preferences.browserMode)
    private val selectedBrowser = preferenceRepository.getStringState(Preferences.selectedBrowser)
    private val inAppBrowserMode = preferenceRepository.getState(Preferences.inAppBrowserMode)
    private val selectedInAppBrowser =
        preferenceRepository.getStringState(Preferences.selectedInAppBrowser)

    private val unifiedPreferredBrowser =
        preferenceRepository.getBooleanState(Preferences.unifiedPreferredBrowser)

    suspend fun resolve(intent: Intent, referrer: Uri?): BottomSheetResult {
        logger.debug("Intent=%s", intent)

        val ignoreLibRedirectExtra = intent.getBooleanExtra(
            LibRedirectDefault.libRedirectIgnore, false
        )

        if (ignoreLibRedirectExtra) {
            intent.extras?.remove(LibRedirectDefault.libRedirectIgnore)
        }

        var uri = intent.getUri(
            useClearUrls.value, useFastForwardRules.value, fastForwardRulesObject
        )

        if (uri == null) {
            logger.debug("Uri is null, something probably went very wrong")
        }

        var followRedirect: Result<RedirectFollower.FollowRedirect>? = null

        if (followRedirects.value && uri != null) {
            val result = redirectFollower.followRedirects(
                uri,
                followRedirectsLocalCache.value,
                fastForwardRulesObject,
                followOnlyKnownTrackers.value,
                followRedirectsExternalService.value,
                followRedirectsTimeout.value
            )

            followRedirect = result
            result.getOrNull()?.let {
                uri = Uri.parse(it.resolvedUrl)
            }
        }

        var libRedirectResult: LibRedirectResolver.LibRedirectResult? = null
        if (enableLibRedirect.value && uri != null && !(ignoreLibRedirectExtra && enableIgnoreLibRedirectButton.value)) {
            libRedirectResult = libRedirectResolver.resolve(uri!!)
            if (libRedirectResult is LibRedirectResolver.LibRedirectResult.Redirected) {
                uri = libRedirectResult.redirectedUri
            }
        }

        val downloadable = if (enableDownloader.value && uri != null) {
            checkIsDownloadable(uri!!, downloaderTimeout.value)
        } else Downloader.DownloadCheckResult.NonDownloadable

        val preferredApp = preferredAppRepository.getByHost(uri)
            ?.toPreferredDisplayActivityInfo(context)
        logger.debug("PreferredApp=%s", preferredApp)

        val lastUsedApps = appSelectionHistoryRepository.getLastUsedForHostGroupedByPackage(uri)

        logger.debug(
            "LastUsedApps=%s",
            lastUsedApps?.toDumpable(
                "packageName", "lastUsed",
                { it },
                { it.toString() },
                PackageProcessor,
                HashProcessor.NoOpProcessor
            )
        )

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

        logger.debug("NewIntent=%s", newIntent)

        val resolvedList: MutableList<ResolveInfo> = context.packageManager
            .queryResolveInfosByIntent(newIntent, true)
            .toMutableList()

        logger.debug("ResolveList=%s", resolvedList)

        val browserMode = if (newIntent.isSchemeTypicallySupportedByBrowsers()) {
            val (mode, selected, repository) = if (!unifiedPreferredBrowser.value && isCustomTab && allowCustomTab) {
                Triple(inAppBrowserMode, selectedInAppBrowser, inAppBrowsersRepository)
            } else Triple(browserMode, selectedBrowser, normalBrowsersRepository)

            browserHandler.handleBrowsers(mode.value, selected.value, repository, resolvedList)
        } else null

        logger.debug("BrowserMode=%s", browserMode)

        val (grouped, filteredItem, showExtended) = BottomSheetGrouper.group(
            context,
            resolvedList,
            lastUsedApps,
            preferredApp?.app,
            !dontShowFilteredItem.value
        )

        val selectedBrowserIsSingleOption =
            browserMode?.browserMode == BrowserHandler.BrowserMode.SelectedBrowser
                    && resolvedList.singleOrNull()?.activityInfo?.componentName() == browserMode.resolveInfo?.activityInfo?.componentName()

        val noBrowsersPresentOnlySingleApp =
            browserMode?.browserMode == BrowserHandler.BrowserMode.None && resolvedList.size == 1

        logger.debug(
            "Grouped=%s, filteredItem=%s, showExtended=%s, selectedBrowserIsSingleOption=%s, noBrowsersPresentOnlySingleApp=%s",
            grouped,
            filteredItem,
            showExtended,
            selectedBrowserIsSingleOption,
            noBrowsersPresentOnlySingleApp
        )

        return BottomSheetResult(
            newIntent,
            uri,
            grouped,
            filteredItem,
            showExtended,
            preferredApp?.app?.alwaysPreferred,
            selectedBrowserIsSingleOption || noBrowsersPresentOnlySingleApp,
            followRedirect,
            libRedirectResult,
            downloadable
        )
    }

    private fun checkIsDownloadable(uri: Uri, connectTimeout: Int): Downloader.DownloadCheckResult {
        if (downloaderCheckUrlMimeType.value) {
            downloader.checkIsNonHtmlFileEnding(uri.toString()).let {
                logger.debug("File ending check result $it")
                if (it.isDownloadable()) return it
            }
        }

        return downloader.isNonHtmlContentUri(uri.toString(), connectTimeout)
    }
}