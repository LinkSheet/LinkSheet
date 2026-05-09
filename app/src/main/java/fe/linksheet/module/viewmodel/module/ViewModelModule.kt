package fe.linksheet.module.viewmodel.module


import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.profile.ProfileFeatureModule
import com.akuleshov7.ktoml.Toml
import fe.gson.GsonQualifier
import fe.linksheet.module.log.DefaultLogModule
import fe.linksheet.module.preference.PreferenceRepositoryModule
import fe.linksheet.module.repository.module.RepositoryModule
import fe.linksheet.module.viewmodel.AboutSettingsViewModel
import fe.linksheet.module.viewmodel.Amp2HtmlSettingsViewModel
import fe.linksheet.module.viewmodel.AppConfigViewModel
import fe.linksheet.module.viewmodel.BottomSheetSettingsViewModel
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.module.viewmodel.CrashHandlerViewerViewModel
import fe.linksheet.module.viewmodel.DevSettingsViewModel
import fe.linksheet.module.viewmodel.DownloaderSettingsViewModel
import fe.linksheet.module.viewmodel.ExperimentsViewModel
import fe.linksheet.module.viewmodel.ExportSettingsViewModel
import fe.linksheet.module.viewmodel.FeatureFlagViewModel
import fe.linksheet.module.viewmodel.FollowRedirectsSettingsViewModel
import fe.linksheet.module.viewmodel.GeneralSettingsViewModel
import fe.linksheet.module.viewmodel.InAppBrowserSettingsViewModel
import fe.linksheet.module.viewmodel.LanguageSettingsViewModel
import fe.linksheet.module.viewmodel.LinksSettingsViewModel
import fe.linksheet.module.viewmodel.LoadDumpedPreferencesViewModel
import fe.linksheet.module.viewmodel.LogSettingsViewModel
import fe.linksheet.module.viewmodel.LogTextSettingsViewModel
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.module.viewmodel.NotificationSettingsViewModel
import fe.linksheet.module.viewmodel.PreferredBrowserViewModel
import fe.linksheet.module.viewmodel.PretendToBeAppSettingsViewModel
import fe.linksheet.module.viewmodel.PreviewSettingsViewModel
import fe.linksheet.module.viewmodel.PrivacySettingsViewModel
import fe.linksheet.module.viewmodel.SelectDomainsConfirmationViewModel
import fe.linksheet.module.viewmodel.SettingsViewModel
import fe.linksheet.module.viewmodel.SingleBrowserViewModel
import fe.linksheet.module.viewmodel.SqlViewModel
import fe.linksheet.module.viewmodel.ThemeSettingsViewModel
import fe.linksheet.module.viewmodel.VerifiedLinkHandlerViewModel
import fe.linksheet.module.viewmodel.VerifiedLinkHandlersViewModel
import fe.linksheet.module.viewmodel.WhitelistedBrowsersViewModel
import fe.linksheet.module.viewmodel.util.LogViewCommon
import fe.linksheet.util.ExportImportUseCase
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
val ViewModelModule = module {
    includes(
        PreferenceRepositoryModule,
        RepositoryModule,
        DefaultLogModule,
        ProfileFeatureModule
    )
    factory {
        LogViewCommon(
            preferenceRepository = get(),
            experimentRepository = get(),
            pasteService = get(),
            gson = get(qualifier(GsonQualifier.Pretty)),
            toml = Toml.Default,
            systemInfoService = get(),
            useCase = get()
        )
    }
    factory {
        ExportImportUseCase(
            repository = get<AppPreferenceRepository>(),
            gson = get(qualifier(GsonQualifier.Pretty)),
            toml = Toml.Default
        )
    }
//    factory{
//        ClipboardUseCase(
//            repository = get<AppPreferenceRepository>(),
//            clipboardManager = getSystemServiceOrThrow<ClipboardManager>(),
//        )
//    }

    viewModelOf(::MainViewModel)
    viewModelOf(::VerifiedLinkHandlersViewModel)
    viewModel { parameters ->
        VerifiedLinkHandlerViewModel(
            packageName = parameters.get(),
            preferenceRepository = get(),
            preferredAppRepository = get(),
            service = get(),
            intentCompat = get()
        )
    }
    viewModelOf(::InAppBrowserSettingsViewModel)
    viewModelOf(::PreferredBrowserViewModel)
    viewModelOf(::BottomSheetSettingsViewModel)
    viewModelOf(::LinksSettingsViewModel)
    viewModelOf(::BottomSheetViewModel)
    viewModelOf(::ThemeSettingsViewModel)
    viewModelOf(::LanguageSettingsViewModel)
    viewModelOf(::FollowRedirectsSettingsViewModel)
    viewModelOf(::DownloaderSettingsViewModel)
    viewModelOf(::LogSettingsViewModel)
    viewModel { parameters ->
        LogTextSettingsViewModel(
            context = get(),
            sessionId = parameters[0],
            logViewCommon = get(),
            preferenceRepository = get(),
            logPersistService = get()
        )
    }
    viewModelOf(::CrashHandlerViewerViewModel)
    viewModelOf(::Amp2HtmlSettingsViewModel)
    viewModelOf(::FeatureFlagViewModel)
    viewModelOf(::PretendToBeAppSettingsViewModel)
    viewModelOf(::GeneralSettingsViewModel)
    viewModelOf(::LoadDumpedPreferencesViewModel)
    viewModelOf(::PrivacySettingsViewModel)
    viewModel {
        ExportSettingsViewModel(
            context = get(),
            preferenceRepository = get(),
            gson = get(qualifier(GsonQualifier.Pretty)),
            clock = get(),
            zoneId = get(),
            useCase = get()
        )
    }
    viewModel {
        AboutSettingsViewModel(
            context = get(),
            gson = get(qualifier(GsonQualifier.Pretty)),
            preferenceRepository = get(),
            infoService = get()
        )
    }
    viewModel {
        DevSettingsViewModel(
            context = get(),
            preferenceRepository = get(),
            experimentRepository = get(),
            shizukuHandler = get(),
            miuiCompatProvider = get(),
            gson = get(qualifier(GsonQualifier.Pretty)),
            systemInfoService = get(),
            logPersistService = get(),
            refineWrapper = get(),
            ioDispatcher = Dispatchers.IO
        )
    }
    viewModelOf(::SettingsViewModel)
    viewModelOf(::NotificationSettingsViewModel)
    viewModelOf(::ExperimentsViewModel)
    viewModelOf(::AppConfigViewModel)
    viewModelOf(::SqlViewModel)
    viewModelOf(::PreviewSettingsViewModel)
    viewModel { parameters ->
        WhitelistedBrowsersViewModel(
            type = parameters.get(),
            useCase = get(),
            normalBrowsersRepository = get(),
            inAppBrowsersRepository = get(),
            preferenceRepository = get()
        )
    }
    viewModel { parameters ->
        SingleBrowserViewModel(
            type = parameters.get(),
            useCase = get(),
            preferenceRepository = get()
        )
    }
    viewModelOf(::SelectDomainsConfirmationViewModel)
}
