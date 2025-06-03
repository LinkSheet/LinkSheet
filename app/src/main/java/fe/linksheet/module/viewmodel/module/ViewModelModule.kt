package fe.linksheet.module.viewmodel.module


import fe.gson.GsonQualifier
import fe.linksheet.module.log.DefaultLogModule
import fe.linksheet.module.preference.preferenceRepositoryModule
import fe.linksheet.module.profile.ProfileSwitcherModule
import fe.linksheet.module.repository.module.repositoryModule
import fe.linksheet.module.viewmodel.*
import fe.linksheet.module.viewmodel.util.LogViewCommon
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
val viewModelModule = module {
    includes(
        preferenceRepositoryModule,
        repositoryModule,
        DefaultLogModule,
        ProfileSwitcherModule
    )
    single {
        LogViewCommon(
            preferenceRepository = get(),
            experimentRepository = get(),
            pasteService = get(),
            gson = get(qualifier(GsonQualifier.Pretty)),
            redactor = get(),
            systemInfoService = get()
        )
    }

    viewModelOf(::MainViewModel)
    viewModelOf(::VerifiedLinkHandlersViewModel)
    viewModelOf(::PreferredAppSettingsViewModel)
    viewModelOf(::InAppBrowserSettingsViewModel)
    viewModelOf(::PreferredBrowserViewModel)
    viewModelOf(::BottomSheetSettingsViewModel)
    viewModelOf(::LinksSettingsViewModel)
    viewModelOf(::LibRedirectSettingsViewModel)
    viewModel { parameters ->
        LibRedirectServiceSettingsViewModel(
            context = get(),
            serviceKey = parameters.get(),
            defaultRepository = get(),
            stateRepository = get(),
            preferenceRepository = get()
        )
    }
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
            zoneId = get()
        )
    }
    viewModel {
        AboutSettingsViewModel(
            context = get(),
            gson = get(qualifier(GsonQualifier.Pretty)),
            preferenceRepository = get()
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
            ioDispatcher = Dispatchers.IO
        )
    }
    viewModelOf(::SettingsViewModel)
    viewModelOf(::NotificationSettingsViewModel)
    viewModelOf(::ExperimentsViewModel)
    viewModelOf(::AppConfigViewModel)
    viewModelOf(::ProfileSwitchingSettingsViewModel)
    viewModelOf(::MarkdownViewModel)
}
