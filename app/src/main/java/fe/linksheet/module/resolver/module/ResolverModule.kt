@file:OptIn(ExperimentalTime::class)

package fe.linksheet.module.resolver.module

import android.app.usage.UsageStatsManager
import fe.composekit.preference.asFunction
import fe.droidkit.koin.getPackageManager
import fe.droidkit.koin.getSystemServiceOrThrow
import fe.linksheet.BuildConfig
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.intent.engine.DefaultLinkEngineIntentResolver
import fe.linksheet.intent.engine.LinkEngineIntentResolver
import fe.linksheet.feature.app.PackageService
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.resolver.*
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlUrlResolver
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectUrlResolver
import fe.linksheet.module.resolver.util.AppSorter
import fe.linksheet.module.resolver.util.DefaultIntentLauncher
import fe.linksheet.module.resolver.util.IntentLauncher
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

val ResolverModule = module {
    single { BrowserResolver(getPackageManager(), get()) }
    single {
        val experimentRepository = get<ExperimentRepository>()
        ImprovedBrowserHandler(
            autoLaunchSingleBrowserExperiment = experimentRepository.asFunction(Experiments.autoLaunchSingleBrowser),
        )
    }
    single {
        AppSorter(
            queryAndAggregateUsageStats = getSystemServiceOrThrow<UsageStatsManager>()::queryAndAggregateUsageStats,
            toAppInfo = get<PackageService>()::toAppInfo,
            clock = get()
        )
    }
    single<IntentLauncher> {
        val appPreferenceRepository = get<AppPreferenceRepository>()
        DefaultIntentLauncher(
            getComponentEnabledSetting = getPackageManager()::getComponentEnabledSetting,
            showAsReferrer = appPreferenceRepository.asFunction(AppPreferences.showLinkSheetAsReferrer),
            selfPackage = BuildConfig.APPLICATION_ID
        )
    }
    singleOf(::InAppBrowserHandler)
    singleOf(::RedirectUrlResolver)
    singleOf(::Amp2HtmlUrlResolver)
    singleOf(::LibRedirectResolver)
    single<IntentResolver> {
        val settings = createSettings(get(), get())
        val experimentRepository = get<ExperimentRepository>()

        val linkEngineIntentResolver = DefaultLinkEngineIntentResolver(
            context = get(),
            logger = createLogger<LinkEngineIntentResolver>(),
            client = get(),
            appSelectionHistoryRepository = get(),
            preferredAppRepository = get(),
            normalBrowsersRepository = get(),
            inAppBrowsersRepository = get(),
            packageService = get(),
            appSorter = get(),
            downloader = get(),
            browserHandler = get(),
            inAppBrowserHandler = get(),
            libRedirectResolver = get(),
            cacheRepository = get(),
            networkStateService = get(),
            privateBrowsingService = get(),
            settings = settings
        )

        IntentResolverDelegate(
            improvedIntentResolver = ImprovedIntentResolver(
                context = get(),
                logger = createLogger<ImprovedIntentResolver>(),
                appSelectionHistoryRepository = get(),
                preferredAppRepository = get(),
                normalBrowsersRepository = get(),
                inAppBrowsersRepository = get(),
                packageInfoService = get(),
                appSorter = get(),
                downloader = get(),
                redirectUrlResolver = get(),
                amp2HtmlResolver = get(),
                browserHandler = get(),
                inAppBrowserHandler = get(),
                libRedirectResolver = get(),
                unfurler = get(),
                networkStateService = get(),
                privateBrowsingService = get(),
                settings = settings
            ),
            linkEngineIntentResolver = linkEngineIntentResolver,
            useLinkEngine = experimentRepository.asFunction(Experiments.linkEngine)
        )
    }
}

data class IntentResolverSettings(
    val useClearUrls: () -> Boolean,
    val useFastForwardRules: () -> Boolean,
    val requestTimeout: () -> Int,
    val dontShowFilteredItem: () -> Boolean,
    val resolveEmbeds: () -> Boolean,
    val autoLaunchSingleBrowser: () -> Boolean,
    val browserSettings: BrowserSettings,
    val libRedirectSettings: LibRedirectSettings,
    val amp2HtmlSettings: Amp2HtmlSettings,
    val followRedirectsSettings: FollowRedirectsSettings,
    val downloaderSettings: DownloaderSettings,
    val previewSettings: PreviewSettings,
)

data class BrowserSettings(
    val inAppBrowserSettings: () -> InAppBrowserHandler.InAppBrowserMode,
    val browserMode: () -> BrowserMode,
    val selectedBrowser: () -> String?,
    val inAppBrowserMode: () -> BrowserMode,
    val selectedInAppBrowser: () -> String?,
    val unifiedPreferredBrowser: () -> Boolean,
)

data class FollowRedirectsSettings(
    val followRedirects: () -> Boolean,
    val followRedirectsSkipBrowser: () -> Boolean,
    val followOnlyKnownTrackers: () -> Boolean,
    val followRedirectsLocalCache: () -> Boolean,
    val followRedirectsExternalService: () -> Boolean,
    val followRedirectsAllowDarknets: () -> Boolean,
    val followRedirectsAllowLocalNetwork: () -> Boolean,
    val manualFollowRedirects: () -> Boolean,
)

data class Amp2HtmlSettings(
    val enableAmp2Html: () -> Boolean,
    val amp2HtmlLocalCache: () -> Boolean,
    val amp2HtmlExternalService: () -> Boolean,
    val amp2HtmlAllowDarknets: () -> Boolean,
    val amp2HtmlAllowLocalNetwork: () -> Boolean,
    val amp2HtmlSkipBrowser: () -> Boolean,
)

data class DownloaderSettings(
    val enableDownloader: () -> Boolean,
    val downloaderCheckUrlMimeType: () -> Boolean,
)

data class LibRedirectSettings(
    val enableIgnoreLibRedirectButton: () -> Boolean,
    val enableLibRedirect: () -> Boolean,
    val libRedirectJsEngine: () -> Boolean,
)

data class PreviewSettings(
    val previewUrl: () -> Boolean,
    val previewUrlSkipBrowser: () -> Boolean,
    val useLocalCache: () -> Boolean = { true }
)

@OptIn(SensitivePreference::class)
fun createSettings(
    prefRepo: AppPreferenceRepository,
    experimentRepository: ExperimentRepository
): IntentResolverSettings {
    return IntentResolverSettings(
        useClearUrls = prefRepo.asFunction(AppPreferences.useClearUrls),
        useFastForwardRules = prefRepo.asFunction(AppPreferences.useFastForwardRules),
        requestTimeout = prefRepo.asFunction(AppPreferences.requestTimeout),
        dontShowFilteredItem = prefRepo.asFunction(AppPreferences.dontShowFilteredItem),
        resolveEmbeds = prefRepo.asFunction(AppPreferences.resolveEmbeds),
        autoLaunchSingleBrowser = experimentRepository.asFunction(Experiments.autoLaunchSingleBrowser),
        browserSettings = BrowserSettings(
            inAppBrowserSettings = prefRepo.asFunction(AppPreferences.inAppBrowserSettings),
            browserMode = prefRepo.asFunction(AppPreferences.browserMode),
            selectedBrowser = prefRepo.asFunction(AppPreferences.selectedBrowser),
            inAppBrowserMode = prefRepo.asFunction(AppPreferences.inAppBrowserMode),
            selectedInAppBrowser = prefRepo.asFunction(AppPreferences.selectedInAppBrowser),
            unifiedPreferredBrowser = prefRepo.asFunction(AppPreferences.unifiedPreferredBrowser),
        ),
        libRedirectSettings = LibRedirectSettings(
            enableIgnoreLibRedirectButton = prefRepo.asFunction(AppPreferences.libRedirect.enableIgnoreLibRedirectButton),
            enableLibRedirect = prefRepo.asFunction(AppPreferences.libRedirect.enable),
            libRedirectJsEngine = experimentRepository.asFunction(Experiments.libRedirectJsEngine)
        ),
        amp2HtmlSettings = Amp2HtmlSettings(
            enableAmp2Html = prefRepo.asFunction(AppPreferences.amp2Html.enable),
            amp2HtmlLocalCache = prefRepo.asFunction(AppPreferences.amp2Html.localCache),
            amp2HtmlExternalService = prefRepo.asFunction(AppPreferences.amp2Html.externalService),
            amp2HtmlAllowDarknets = prefRepo.asFunction(AppPreferences.amp2Html.allowDarknets),
            amp2HtmlAllowLocalNetwork = prefRepo.asFunction(AppPreferences.amp2Html.allowLocalNetwork),
            amp2HtmlSkipBrowser = prefRepo.asFunction(AppPreferences.amp2Html.skipBrowser),
        ),
        followRedirectsSettings = FollowRedirectsSettings(
            followRedirects = prefRepo.asFunction(AppPreferences.followRedirects.enable),
            followRedirectsSkipBrowser = prefRepo.asFunction(AppPreferences.followRedirects.skipBrowser),
            manualFollowRedirects = prefRepo.asFunction(Experiments.manualFollowRedirects),
            followOnlyKnownTrackers = prefRepo.asFunction(AppPreferences.followRedirects.onlyKnownTrackers),
            followRedirectsLocalCache = prefRepo.asFunction(AppPreferences.followRedirects.localCache),
            followRedirectsExternalService = prefRepo.asFunction(AppPreferences.followRedirects.externalService),
            followRedirectsAllowDarknets = prefRepo.asFunction(AppPreferences.followRedirects.allowDarknets),
            followRedirectsAllowLocalNetwork = prefRepo.asFunction(AppPreferences.followRedirects.allowLocalNetwork),
        ),
        downloaderSettings = DownloaderSettings(
            enableDownloader = prefRepo.asFunction(AppPreferences.downloader.enable),
            downloaderCheckUrlMimeType = prefRepo.asFunction(AppPreferences.downloader.checkUrlMimeType),
        ),
        previewSettings = PreviewSettings(
            previewUrl = prefRepo.asFunction(AppPreferences.openGraphPreview.enable),
            previewUrlSkipBrowser = prefRepo.asFunction(AppPreferences.openGraphPreview.skipBrowser),
        ),
    )
}
