package fe.linksheet.koin

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import app.linksheet.api.BuildConstants
import app.linksheet.api.BuildInfo
import app.linksheet.api.CachedRequest
import app.linksheet.api.RefineWrapper
import app.linksheet.api.SystemInfoService
import app.linksheet.api.SystemProperties
import app.linksheet.compose.debug.DebugMenuSlotProvider
import app.linksheet.feature.analytics.service.BaseAnalyticsService
import app.linksheet.feature.app.core.MetaDataHandler
import app.linksheet.feature.app.core.PackageIntentHandler
import app.linksheet.feature.app.core.PackageLabelService
import app.linksheet.feature.app.core.PackageLauncherService
import app.linksheet.feature.app.core.domain.DomainVerificationManagerCompat
import app.linksheet.feature.app.usecase.AllAppsUseCase
import app.linksheet.feature.app.usecase.BrowsersUseCase
import app.linksheet.feature.app.usecase.DomainVerificationUseCase
import app.linksheet.feature.browser.usecase.PrivateBrowserUseCase
import app.linksheet.feature.devicecompat.miui.MiuiCompat
import app.linksheet.feature.devicecompat.miui.MiuiCompatProvider
import app.linksheet.feature.devicecompat.oneui.OneUiCompat
import app.linksheet.feature.downloader.Downloader
import app.linksheet.feature.engine.database.repository.CacheRepository
import app.linksheet.feature.libredirect.LibRedirectResolver
import app.linksheet.feature.libredirect.database.dao.LibRedirectDefaultDao
import app.linksheet.feature.libredirect.database.dao.LibRedirectServiceStateDao
import app.linksheet.feature.libredirect.database.dao.LibRedirectUserInstanceDao
import app.linksheet.feature.libredirect.database.repository.LibRedirectDefaultRepository
import app.linksheet.feature.libredirect.database.repository.LibRedirectStateRepository
import app.linksheet.feature.libredirect.database.repository.LibRedirectUserInstanceRepository
import app.linksheet.feature.libredirect.preference.LibRedirectPreferences
import app.linksheet.feature.libredirect.viewmodel.LibRedirectServiceSettingsViewModel
import app.linksheet.feature.libredirect.viewmodel.LibRedirectSettingsViewModel
import app.linksheet.feature.profile.service.ProfileService
import app.linksheet.feature.remoteconfig.preference.RemoteConfigRepository
import app.linksheet.feature.remoteconfig.service.RemoteConfigClient
import app.linksheet.feature.remoteconfig.service.RemoteConfigService
import app.linksheet.feature.shizuku.ShizukuService
import app.linksheet.feature.shizuku.preference.ShizukuPreferences
import app.linksheet.feature.shizuku.viewmodel.ShizukuSettingsViewModel
import app.linksheet.feature.wiki.database.dao.WikiCacheDao
import app.linksheet.feature.wiki.database.repository.WikiCacheRepository
import app.linksheet.feature.wiki.viewmodel.MarkdownViewModel
import app.linksheet.mozilla.components.support.base.log.logger.Logger
import app.linksheet.testlib.koin.definition
import app.linksheet.testlib.koin.injectedParameters
import app.linksheet.testlib.koin.verifyAll
import coil3.ImageLoader
import com.akuleshov7.ktoml.Toml
import com.google.gson.Gson
import fe.android.preference.helper.PreferenceRepository
import fe.httpkt.HttpData
import fe.httpkt.Request
import fe.httpkt.internal.HttpInternals
import fe.linksheet.LinkSheetApp
import fe.linksheet.module.database.dao.AppSelectionHistoryDao
import fe.linksheet.module.database.dao.DisableInAppBrowserInSelectedDao
import fe.linksheet.module.database.dao.PreferredAppDao
import fe.linksheet.module.database.dao.resolver.Amp2HtmlMappingDao
import fe.linksheet.module.database.dao.resolver.ResolvedRedirectDao
import fe.linksheet.module.database.dao.whitelisted.WhitelistedInAppBrowsersDao
import fe.linksheet.module.database.dao.whitelisted.WhitelistedNormalBrowsersDao
import fe.linksheet.module.language.AppLocaleService
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.paste.PasteService
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.DisableInAppBrowserInSelectedRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.resolver.urlresolver.RealCachedRequest
import fe.linksheet.module.resolver.urlresolver.RemoteResolver
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlResolveRequest
import fe.linksheet.module.resolver.urlresolver.base.LocalTask
import fe.linksheet.module.resolver.urlresolver.base.UrlResolver
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectResolveRequest
import fe.linksheet.module.resolver.util.AppSorter
import fe.linksheet.module.resolver.util.IntentLauncher
import fe.linksheet.module.shizuku.ShizukuServiceConnection
import fe.linksheet.module.statistic.StatisticsService
import fe.linksheet.module.versiontracker.VersionTracker
import fe.linksheet.module.viewmodel.AboutSettingsViewModel
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.module.viewmodel.DevSettingsViewModel
import fe.linksheet.module.viewmodel.ExportSettingsViewModel
import fe.linksheet.module.viewmodel.InAppBrowserSettingsViewModel
import fe.linksheet.module.viewmodel.LanguageSettingsViewModel
import fe.linksheet.module.viewmodel.LogTextSettingsViewModel
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.module.viewmodel.PreferredBrowserViewModel
import fe.linksheet.module.viewmodel.PrivacySettingsViewModel
import fe.linksheet.module.viewmodel.SelectDomainsConfirmationViewModel
import fe.linksheet.module.viewmodel.SettingsViewModel
import fe.linksheet.module.viewmodel.SingleBrowserViewModel
import fe.linksheet.module.viewmodel.ThemeSettingsViewModel
import fe.linksheet.module.viewmodel.VerifiedLinkHandlerViewModel
import fe.linksheet.module.viewmodel.VerifiedLinkHandlersViewModel
import fe.linksheet.module.viewmodel.WhitelistedBrowsersViewModel
import fe.linksheet.module.viewmodel.util.LogViewCommon
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.util.ExportImportUseCase
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.robolectric.annotation.Config
import java.time.ZoneId
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(KoinExperimentalAPI::class, ExperimentalTime::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
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
            BuildConstants::class,
            BuildInfo::class
        ),
        definition<AllAppsUseCase>(
            DomainVerificationManagerCompat::class,
            PackageLabelService::class,
            PackageLauncherService::class,
            PackageIntentHandler::class
        ),
        definition<DomainVerificationUseCase>(DomainVerificationManagerCompat::class),
        definition<RealCachedRequest>(Request::class),
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
        definition<InAppBrowserHandler>(DisableInAppBrowserInSelectedRepository::class),
        definition<LibRedirectDefaultRepository>(LibRedirectDefaultDao::class),
        definition<LibRedirectStateRepository>(LibRedirectServiceStateDao::class),
        definition<PreferredAppRepository>(PreferredAppDao::class),
        definition<DisableInAppBrowserInSelectedRepository>(DisableInAppBrowserInSelectedDao::class),
        definition<WhitelistedNormalBrowsersRepository>(WhitelistedNormalBrowsersDao::class),
        definition<WhitelistedInAppBrowsersRepository>(WhitelistedInAppBrowsersDao::class),
        definition<AppSelectionHistoryRepository>(AppSelectionHistoryDao::class),
        definition<ResolvedRedirectRepository>(ResolvedRedirectDao::class),
        definition<Amp2HtmlRepository>(Amp2HtmlMappingDao::class),
        definition<WikiCacheRepository>(WikiCacheDao::class),
        definition<LibRedirectSettingsViewModel>(
            AppPreferenceRepository::class,
            LibRedirectPreferences::class
        ),
        definition<LibRedirectResolver>(
            LibRedirectDefaultRepository::class,
            LibRedirectStateRepository::class
        ),
        definition<LogViewCommon>(
            PasteService::class,
            Gson::class,
            Toml::class,
            SystemInfoService::class,
            ExportImportUseCase::class
        ),
        definition<UrlResolver>(
            LocalTask.Redirector::class,
            LocalTask.Amp2Html::class,
            RemoteResolver::class
        ),
        definition<VersionTracker>(BaseAnalyticsService::class, SystemInfoService::class),
        definition<MainViewModel>(
            BaseAnalyticsService::class,
            MiuiCompatProvider::class,
            MiuiCompat::class,
            DebugMenuSlotProvider::class,
            PackageIntentHandler::class,
        ),
        definition<VerifiedLinkHandlersViewModel>(
            ShizukuServiceConnection::class,
            DomainVerificationUseCase::class,
            OneUiCompat::class
        ),
        definition<PreferredBrowserViewModel>(BrowsersUseCase::class),
        definition<PrivacySettingsViewModel>(BaseAnalyticsService::class),
        definition<ExportSettingsViewModel>(Gson::class, Clock::class, ZoneId::class),
        definition<AboutSettingsViewModel>(Gson::class, SystemInfoService::class),
        definition<DevSettingsViewModel>(
            ShizukuServiceConnection::class,
            MiuiCompatProvider::class,
            Gson::class,
            SystemInfoService::class,
            LogPersistService::class,
            RefineWrapper::class,
            CoroutineDispatcher::class
        ),
        definition<LogTextSettingsViewModel>(),
        definition<MarkdownViewModel>(Request::class, WikiCacheRepository::class),
        definition<LibRedirectServiceSettingsViewModel>(
            CoroutineDispatcher::class
        ),
        definition<BottomSheetViewModel>(
            ImageLoader::class,
            IntentResolver::class,
            IntentLauncher::class,
            MetaDataHandler::class,
            PrivateBrowserUseCase::class
        ),
        definition<Request>(
            HttpData.Builder::class,
            HttpData::class,
            HttpInternals::class,
            HttpData::class
        ),
        definition<Downloader>(HttpClient::class),
        definition<StatisticsService>(AppPreferenceRepository::class),
        definition<AppLocaleService>(List::class),
        definition<LanguageSettingsViewModel>(AppLocaleService::class),
        definition<SettingsViewModel>(AppLocaleService::class),
        definition<ThemeSettingsViewModel>(RemoteConfigRepository::class),
        definition<RedirectResolveRequest>(HttpClient::class),
        definition<Amp2HtmlResolveRequest>(HttpClient::class),
        definition<VerifiedLinkHandlerViewModel>(
            DomainVerificationUseCase::class,
            OneUiCompat::class
        ),
        definition<ShizukuSettingsViewModel>(
            ShizukuService::class,
            AppPreferenceRepository::class,
            ShizukuPreferences::class
        ),
        definition<InAppBrowserSettingsViewModel>(AllAppsUseCase::class),
        definition<WhitelistedBrowsersViewModel>(
            PreferredBrowserViewModel.BrowserType::class,
            BrowsersUseCase::class
        ),
        definition<SingleBrowserViewModel>(
            PreferredBrowserViewModel.BrowserType::class,
            BrowsersUseCase::class
        ),
        definition<SelectDomainsConfirmationViewModel>(
            AllAppsUseCase::class
        ),
        definition<LibRedirectUserInstanceRepository>(LibRedirectUserInstanceDao::class),
        definition<ProfileService>(MetaDataHandler::class),
        definition<ExportImportUseCase>(PreferenceRepository::class, Gson::class, Toml::class),
        definition<RemoteConfigClient>(HttpClient::class),
        definition<RemoteConfigService>(WorkManager::class, StateFlow::class),
    )

    @Test
    fun test() {
        LinkSheetApp().provideKoinModules().verifyAll(
            extraTypes = extraTypes,
            injections = injections
        )
    }
}
