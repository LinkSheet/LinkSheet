package fe.linksheet.koin

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import fe.httpkt.HttpData
import fe.httpkt.Request
import fe.httpkt.internal.HttpInternals
import fe.linksheet.LinkSheetApp
import fe.linksheet.UnitTest
import fe.linksheet.module.analytics.AnalyticsClient
import fe.linksheet.module.analytics.BaseAnalyticsService
import fe.linksheet.module.app.PackageService
import fe.linksheet.module.app.`package`.PackageBrowserService
import fe.linksheet.module.app.`package`.PackageLabelService
import fe.linksheet.module.app.`package`.PackageLauncherService
import fe.linksheet.module.app.`package`.domain.DomainVerificationManagerCompat
import fe.linksheet.module.debug.DebugMenuSlotProvider
import fe.linksheet.module.devicecompat.miui.MiuiCompat
import fe.linksheet.module.devicecompat.miui.MiuiCompatProvider
import fe.linksheet.module.devicecompat.samsung.SamsungIntentCompat
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.log.internal.LoggerDelegate
import fe.linksheet.module.network.NetworkStateService
import fe.linksheet.module.paste.PasteService
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.state.AppStateService
import fe.linksheet.module.profile.ProfileSwitcher
import fe.linksheet.module.redactor.LogHasher
import fe.linksheet.module.redactor.Redactor
import fe.linksheet.module.repository.DisableInAppBrowserInSelectedRepository
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
import fe.linksheet.module.resolver.BrowserResolver
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlResolveRequest
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlUrlResolver
import fe.linksheet.module.resolver.urlresolver.base.AllRemoteResolveRequest
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectResolveRequest
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectUrlResolver
import fe.linksheet.module.shizuku.ShizukuHandler
import fe.linksheet.module.statistic.StatisticsService
import fe.linksheet.module.systeminfo.BuildConstants
import fe.linksheet.module.systeminfo.SystemInfoService
import fe.linksheet.module.systeminfo.SystemProperties
import fe.linksheet.module.versiontracker.VersionTracker
import fe.linksheet.module.viewmodel.AboutSettingsViewModel
import fe.linksheet.module.viewmodel.BottomSheetSettingsViewModel
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.module.viewmodel.CrashHandlerViewerViewModel
import fe.linksheet.module.viewmodel.DevSettingsViewModel
import fe.linksheet.module.viewmodel.ExportSettingsViewModel
import fe.linksheet.module.viewmodel.LogSettingsViewModel
import fe.linksheet.module.viewmodel.LogTextSettingsViewModel
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.module.viewmodel.MarkdownViewModel
import fe.linksheet.module.viewmodel.PreferredAppSettingsViewModel
import fe.linksheet.module.viewmodel.PreferredBrowserViewModel
import fe.linksheet.module.viewmodel.PrivacySettingsViewModel
import fe.linksheet.module.viewmodel.ProfileSwitchingSettingsViewModel
import fe.linksheet.module.viewmodel.VerifiedLinkHandlersViewModel
import fe.linksheet.module.viewmodel.util.LogViewCommon
import okhttp3.OkHttpClient
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verifyAll

@OptIn(KoinExperimentalAPI::class)
internal class KoinModuleCheckTest : UnitTest {
    private val extraTypes = listOf(
        Context::class,
        PackageManager::class,
        ConnectivityManager::class,
        Function0::class,
        Function1::class,
        Application::class,
        SavedStateHandle::class,
        Logger::class,
    )

    private val injections = injectedParameters(
        definition<SystemInfoService>(
            SystemProperties::class,
            BuildConstants::class
        ),
        definition<PackageService>(
            DomainVerificationManagerCompat::class,
            PackageLabelService::class,
            PackageLauncherService::class,
            PackageBrowserService::class,
        ),
        definition<Redactor>(LogHasher::class),
        definition<Logger>(LoggerDelegate::class),
        definition<CachedRequest>(Request::class),
        definition<RedirectResolveRequest>(
            Request::class,
            CachedRequest::class,
            OkHttpClient::class
        ),
        definition<Amp2HtmlResolveRequest>(
            Request::class,
            CachedRequest::class,
            OkHttpClient::class
        ),
        definition<AllRemoteResolveRequest>(Request::class),
        definition<BrowserResolver>( PackageService::class),
        definition<InAppBrowserHandler>(DisableInAppBrowserInSelectedRepository::class),
        definition<RedirectUrlResolver>(
            RedirectResolveRequest::class, ResolvedRedirectRepository::class
        ),
        definition<Amp2HtmlUrlResolver>(
            Amp2HtmlResolveRequest::class,
            Amp2HtmlRepository::class
        ),
        definition<LibRedirectResolver>(
            LibRedirectDefaultRepository::class,
            LibRedirectStateRepository::class
        ),
        definition<LogViewCommon>(
            PasteService::class,
            Gson::class,
            Redactor::class,
            SystemInfoService::class
        ),
        definition<VersionTracker>(BaseAnalyticsService::class),
        definition<MainViewModel>(
            BrowserResolver::class,
            BaseAnalyticsService::class,
            MiuiCompatProvider::class,
            MiuiCompat::class,
            DebugMenuSlotProvider::class
        ),
        definition<VerifiedLinkHandlersViewModel>(
            ShizukuHandler::class,
            PackageService::class,
            SamsungIntentCompat::class
        ),
        definition<PreferredAppSettingsViewModel>(PackageService::class),
        definition<PreferredBrowserViewModel>(BrowserResolver::class),
        definition<BottomSheetViewModel>(IntentResolver::class),
        definition<PrivacySettingsViewModel>(BaseAnalyticsService::class),
        definition<ExportSettingsViewModel>(Gson::class),
        definition<AboutSettingsViewModel>(Gson::class),
        definition<DevSettingsViewModel>(
            ShizukuHandler::class,
            MiuiCompatProvider::class,
            Gson::class,
            SystemInfoService::class
        ),
        definition<MarkdownViewModel>(Request::class),
        definition<Request>(HttpData.Builder::class, HttpData::class, HttpInternals::class, HttpData::class),
        definition<Downloader>(CachedRequest::class),
        definition<StatisticsService>(AppPreferenceRepository::class)
    )

    @Test
    fun test() {
        LinkSheetApp().provideKoinModules().verifyAll(
            extraTypes = extraTypes,
            injections = injections
        )
    }
}
