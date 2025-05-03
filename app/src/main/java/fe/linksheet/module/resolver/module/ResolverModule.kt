package fe.linksheet.module.resolver.module

import android.app.usage.UsageStatsManager
import fe.composekit.preference.asFunction
import fe.droidkit.koin.getPackageManager
import fe.droidkit.koin.getSystemServiceOrThrow
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.module.app.PackageService
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
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val resolverModule = module {
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
            toAppInfo = get<PackageService>()::toAppInfo
        )
    }
    singleOf(::InAppBrowserHandler)
    singleOf(::RedirectUrlResolver)
    singleOf(::Amp2HtmlUrlResolver)
    singleOf(::LibRedirectResolver)
    single<IntentResolver> {
        val settings = createSettings(get(), get())

        ImprovedIntentResolver(
            get(),
            createLogger<ImprovedIntentResolver>(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            settings = settings
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
)

data class Amp2HtmlSettings(
    val enableAmp2Html: () -> Boolean,
    val amp2HtmlLocalCache: () -> Boolean,
    val amp2HtmlExternalService: () -> Boolean,
    val amp2HtmlAllowDarknets: () -> Boolean,
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
)


@OptIn(SensitivePreference::class)
fun createSettings(
    prefRepo: AppPreferenceRepository,
    experimentRepository: ExperimentRepository
) : IntentResolverSettings{
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
            enableIgnoreLibRedirectButton = prefRepo.asFunction(AppPreferences.enableIgnoreLibRedirectButton),
            enableLibRedirect = prefRepo.asFunction(AppPreferences.enableLibRedirect),
            libRedirectJsEngine = experimentRepository.asFunction(Experiments.libRedirectJsEngine)
        ),
        amp2HtmlSettings = Amp2HtmlSettings(
            enableAmp2Html = prefRepo.asFunction(AppPreferences.enableAmp2Html),
            amp2HtmlLocalCache = prefRepo.asFunction(AppPreferences.amp2HtmlLocalCache),
            amp2HtmlExternalService = prefRepo.asFunction(AppPreferences.amp2HtmlExternalService),
            amp2HtmlAllowDarknets = prefRepo.asFunction(AppPreferences.amp2HtmlAllowDarknets),
            amp2HtmlSkipBrowser = prefRepo.asFunction(AppPreferences.amp2HtmlSkipBrowser),
        ),
        followRedirectsSettings = FollowRedirectsSettings(
            followRedirects = prefRepo.asFunction(AppPreferences.followRedirects),
            followRedirectsSkipBrowser = prefRepo.asFunction(AppPreferences.followRedirectsSkipBrowser),
            followOnlyKnownTrackers = prefRepo.asFunction(AppPreferences.followOnlyKnownTrackers),
            followRedirectsLocalCache = prefRepo.asFunction(AppPreferences.followRedirectsLocalCache),
            followRedirectsExternalService = prefRepo.asFunction(AppPreferences.followRedirectsExternalService),
            followRedirectsAllowDarknets = prefRepo.asFunction(AppPreferences.followRedirectsAllowDarknets),
        ),
        downloaderSettings = DownloaderSettings(
            enableDownloader = prefRepo.asFunction(AppPreferences.enableDownloader),
            downloaderCheckUrlMimeType = prefRepo.asFunction(AppPreferences.downloaderCheckUrlMimeType),
        ),
        previewSettings = PreviewSettings(
            previewUrl = experimentRepository.asFunction(Experiments.urlPreview),
            previewUrlSkipBrowser = experimentRepository.asFunction(Experiments.urlPreviewSkipBrowser),
        ),
    )
}
