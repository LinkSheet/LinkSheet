package fe.linksheet.module.resolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.tasomaniac.openwith.resolver.BottomSheetResult
import fe.fastforwardkt.FastForwardLoader
import fe.libredirectkt.LibRedirect
import fe.libredirectkt.LibRedirectLoader
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.extension.componentName
import fe.linksheet.extension.getUri
import fe.linksheet.extension.isSchemeTypicallySupportedByBrowsers
import fe.linksheet.extension.newIntent
import fe.linksheet.extension.queryResolveInfosByIntent
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.ResolvedRedirectRepository
import fe.linksheet.resolver.BottomSheetGrouper
import fe.linksheet.util.io
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class IntentResolver(
    val context: Context,
    val preferenceRepository: PreferenceRepository,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val downloader: Downloader,
    private val redirectFollower: RedirectFollower,
    private val browserHandler: BrowserHandler,
    private val inAppBrowserHandler: InAppBrowserHandler,
    private val libRedirectResolver: LibRedirectResolver
) {
    private val fastForwardRulesObject by lazy { FastForwardLoader.loadBuiltInFastForwardRules() }

    private val useClearUrls = preferenceRepository.getBoolean(Preferences.useClearUrls)
    private var useFastForwardRules = preferenceRepository.getBoolean(
        Preferences.useFastForwardRules
    )

    private var enableLibRedirect = preferenceRepository.getBoolean(Preferences.enableLibRedirect)
    private val followRedirects = preferenceRepository.getBoolean(Preferences.followRedirects)

    private val followOnlyKnownTrackers = preferenceRepository.getBoolean(Preferences.followOnlyKnownTrackers)
    private val followRedirectsLocalCache = preferenceRepository.getBoolean(
        Preferences.followRedirectsLocalCache
    )

    private var enableDownloader = preferenceRepository.getBoolean(Preferences.enableDownloader)
    private var downloaderCheckUrlMimeType = preferenceRepository.getBoolean(
        Preferences.downloaderCheckUrlMimeType
    )

    val theme = preferenceRepository.get(Preferences.theme)
    private val dontShowFilteredItem = preferenceRepository.getBoolean(
        Preferences.dontShowFilteredItem
    )

    private val inAppBrowserMode = preferenceRepository.get(Preferences.inAppBrowserMode)
    private val browserMode = preferenceRepository.get(Preferences.browserMode)
    private val selectedBrowser = preferenceRepository.getString(Preferences.selectedBrowser)


    suspend fun resolve(intent: Intent, referrer: Uri?): BottomSheetResult {
        Timber.tag("ResolveIntents").d("Intent: $intent")

        var uri = intent.getUri(useClearUrls, useFastForwardRules, fastForwardRulesObject)
        if(uri == null){
            Timber.tag("ResolveIntents").d("Uri is null, something probably went very wrong")
        }

        var followRedirect: RedirectFollower.FollowRedirect? = null

        if (followRedirects && uri != null) {
            redirectFollower.followRedirects(
                uri,
                followRedirectsLocalCache,
                fastForwardRulesObject,
                followOnlyKnownTrackers,
                followRedirectsLocalCache
            ).getOrNull()?.let {
                followRedirect = it
                uri = Uri.parse(it.resolvedUrl)
            }
        }

        if (enableLibRedirect && uri != null) {
            uri = libRedirectResolver.resolve(uri!!)
        }

        val downloadable = if (enableDownloader && uri != null) {
            checkIsDownloadable(uri!!)
        } else Downloader.DownloadCheckResult.NonDownloadable

        val preferredApp = preferredAppRepository.getByHost(uri) ?.toPreferredDisplayActivityInfo(context)

        Timber.tag("ResolveIntents").d("PreferredApp: $preferredApp")

        val hostHistory = appSelectionHistoryRepository.resolveHostHistory(uri)

        Timber.tag("ResolveIntents").d("HostHistory: $hostHistory")
        Timber.tag("ResolveIntents").d("SourceIntent: $intent ${intent.extras}")

        val isCustomTab = intent.hasExtra(CustomTabsIntent.EXTRA_SESSION)
        val allowCustomTab = inAppBrowserHandler.shouldAllowCustomTab(referrer, inAppBrowserMode)

        val newIntent = intent.newIntent(uri, !isCustomTab || !allowCustomTab)
        if (allowCustomTab) {
            newIntent.extras?.keySet()?.filter { !it.contains("customtabs") }?.forEach { key ->
                Timber.tag("ResolveIntents").d("CustomTab: Remove extra: $key")
                newIntent.removeExtra(key)
            }
        }

        Timber.tag("ResolveIntents").d("NewIntent: $newIntent ${newIntent.extras}")

        val resolvedList = context.packageManager
            .queryResolveInfosByIntent(newIntent, true)
            .toMutableList()

        Timber.tag("ResolveIntents").d("ResolveListPreSort: $resolvedList")

        Timber.tag("ResolveIntents")
            .d("PreferredApp ComponentName: ${preferredApp?.app?.componentName}")

        val browserMode = if (newIntent.isSchemeTypicallySupportedByBrowsers()) {
            browserHandler.handleBrowsers(browserMode, selectedBrowser, resolvedList)
        } else null

        Timber.tag("ResolveIntents").d("BrowserMode: $browserMode")

        val (grouped, filteredItem, showExtended) = BottomSheetGrouper.group(
            context,
            resolvedList,
            hostHistory,
            preferredApp?.app,
            !dontShowFilteredItem
        )

        val selectedBrowserIsSingleOption =
            browserMode?.first == BrowserHandler.BrowserMode.SelectedBrowser
                    && resolvedList.singleOrNull()?.activityInfo?.componentName() == browserMode.second?.activityInfo?.componentName()

        val noBrowsersPresentOnlySingleApp =
            browserMode?.first == BrowserHandler.BrowserMode.None && resolvedList.size == 1


        Timber.tag("ResolveIntents").d(
            "Grouped: $grouped, filteredItem: $filteredItem, showExtended: $showExtended, selectedBrowserIsSingleOption: $selectedBrowserIsSingleOption"
        )

        return BottomSheetResult(
            uri,
            grouped,
            filteredItem,
            showExtended,
            preferredApp?.app?.alwaysPreferred,
            selectedBrowserIsSingleOption || noBrowsersPresentOnlySingleApp,
            followRedirect,
            downloadable
        )
    }

    private fun checkIsDownloadable(uri: Uri): Downloader.DownloadCheckResult {
        if (downloaderCheckUrlMimeType) {
            downloader.checkIsNonHtmlFileEnding(uri.toString()).let {
                Timber.tag("CheckIsDownloadable").d("File ending check result $it")
                if (it.isDownloadable()) return it
            }
        }

        return downloader.isNonHtmlContentUri(uri.toString())
    }
}