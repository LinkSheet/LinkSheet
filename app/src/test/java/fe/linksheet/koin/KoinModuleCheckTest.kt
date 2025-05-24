package fe.linksheet.koin

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import coil3.ImageLoader
import com.google.gson.Gson
import fe.httpkt.HttpData
import fe.httpkt.Request
import fe.httpkt.internal.HttpInternals
import fe.linksheet.LinkSheetApp
import fe.linksheet.feature.libredirect.LibRedirectSettingsUseCase
import fe.linksheet.module.analytics.BaseAnalyticsService
import fe.linksheet.module.app.PackageService
import fe.linksheet.module.app.`package`.PackageIntentHandler
import fe.linksheet.module.app.`package`.PackageLabelService
import fe.linksheet.module.app.`package`.PackageLauncherService
import fe.linksheet.module.app.`package`.domain.DomainVerificationManagerCompat
import fe.linksheet.module.debug.DebugMenuSlotProvider
import fe.linksheet.module.devicecompat.miui.MiuiCompat
import fe.linksheet.module.devicecompat.miui.MiuiCompatProvider
import fe.linksheet.module.devicecompat.oneui.OneUiCompat
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.language.AppLocaleService
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.log.internal.LoggerDelegate
import fe.linksheet.module.paste.PasteService
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.redactor.LogHasher
import fe.linksheet.module.redactor.Redactor
import fe.linksheet.module.remoteconfig.RemoteConfigRepository
import fe.linksheet.module.repository.CacheRepository
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
import fe.linksheet.module.resolver.util.AppSorter
import fe.linksheet.module.resolver.util.IntentLauncher
import fe.linksheet.module.shizuku.ShizukuHandler
import fe.linksheet.module.statistic.StatisticsService
import fe.linksheet.module.systeminfo.BuildConstants
import fe.linksheet.module.systeminfo.SystemInfoService
import fe.linksheet.module.systeminfo.SystemProperties
import fe.linksheet.module.versiontracker.VersionTracker
import fe.linksheet.module.viewmodel.*
import fe.linksheet.module.viewmodel.util.LogViewCommon
import fe.linksheet.module.workmanager.WorkDelegatorService
import fe.linksheet.testlib.core.BaseUnitTest
import io.ktor.client.*
import io.ktor.client.engine.*
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verifyAll
import java.time.ZoneId
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(KoinExperimentalAPI::class, ExperimentalTime::class)
internal class KoinModuleCheckTest : BaseUnitTest {
    private val extraTypes = listOf(
        Context::class,
        PackageManager::class,
        ConnectivityManager::class,
        Function0::class,
        Function1::class,
        Function2::class,
        Application::class,
        SavedStateHandle::class,
        Logger::class,
        WorkerParameters::class,
        Duration::class,
        List::class,
        Clock::class,
        ZoneId::class,
        // TODO: Hook up CacheRepository to DI, then remove here
        CacheRepository::class
    )

    private val injections = injectedParameters(
        definition<AppSorter>(Clock::class),
        definition<HttpClient>(HttpClientEngine::class, HttpClientConfig::class),
        definition<SystemInfoService>(
            SystemProperties::class,
            BuildConstants::class
        ),
        definition<PackageService>(
            DomainVerificationManagerCompat::class,
            PackageLabelService::class,
            PackageLauncherService::class,
            PackageIntentHandler::class
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
            OkHttpClient::class,
        ),
        definition<AllRemoteResolveRequest>(Request::class),
        definition<BrowserResolver>(PackageService::class),
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
        definition<VersionTracker>(BaseAnalyticsService::class, SystemInfoService::class),
        definition<MainViewModel>(
            BrowserResolver::class,
            BaseAnalyticsService::class,
            MiuiCompatProvider::class,
            MiuiCompat::class,
            DebugMenuSlotProvider::class,
            PackageIntentHandler::class,
            WorkDelegatorService::class
        ),
        definition<VerifiedLinkHandlersViewModel>(
            ShizukuHandler::class,
            PackageService::class,
            OneUiCompat::class
        ),
        definition<PreferredAppSettingsViewModel>(PackageService::class),
        definition<PreferredBrowserViewModel>(BrowserResolver::class),
        definition<PrivacySettingsViewModel>(BaseAnalyticsService::class),
        definition<ExportSettingsViewModel>(Gson::class, Clock::class, ZoneId::class),
        definition<AboutSettingsViewModel>(Gson::class),
        definition<DevSettingsViewModel>(
            ShizukuHandler::class,
            MiuiCompatProvider::class,
            Gson::class,
            SystemInfoService::class,
            LogPersistService::class,
            CoroutineDispatcher::class
        ),
        definition<LogTextSettingsViewModel>(),
        definition<MarkdownViewModel>(Request::class),
        definition<LibRedirectServiceSettingsViewModel>(LibRedirectSettingsUseCase::class),
        definition<BottomSheetViewModel>(ImageLoader::class, IntentResolver::class, IntentLauncher::class),
        definition<Request>(HttpData.Builder::class, HttpData::class, HttpInternals::class, HttpData::class),
        definition<Downloader>(CachedRequest::class),
        definition<StatisticsService>(AppPreferenceRepository::class),
        definition<AppLocaleService>(List::class),
        definition<LanguageSettingsViewModel>(AppLocaleService::class),
        definition<SettingsViewModel>(AppLocaleService::class),
        definition<ThemeSettingsViewModel>(RemoteConfigRepository::class),
        definition<WorkDelegatorService>(WorkManager::class)
    )

    @Test
    fun test() {
        LinkSheetApp().provideKoinModules().verifyAll(
            extraTypes = extraTypes,
            injections = injections
        )
    }
}
