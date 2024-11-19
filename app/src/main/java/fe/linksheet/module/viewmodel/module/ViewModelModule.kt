package fe.linksheet.module.viewmodel.module


import fe.linksheet.module.viewmodel.*
import fe.linksheet.module.viewmodel.util.LogViewCommon
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    singleOf(::LogViewCommon)

    viewModelOf(::MainViewModel)
    viewModelOf(::VerifiedLinkHandlersViewModel)
    viewModelOf(::PreferredAppSettingsViewModel)
    viewModelOf(::InAppBrowserSettingsViewModel)
    viewModelOf(::PreferredBrowserViewModel)
    viewModelOf(::BottomSheetSettingsViewModel)
    viewModelOf(::LinksSettingsViewModel)
    viewModelOf(::LibRedirectSettingsViewModel)
    viewModelOf(::LibRedirectServiceSettingsViewModel)
    viewModelOf(::BottomSheetViewModel)
    viewModelOf(::ThemeSettingsViewModel)
    viewModelOf(::FollowRedirectsSettingsViewModel)
    viewModelOf(::DownloaderSettingsViewModel)
    viewModelOf(::LogSettingsViewModel)
    viewModelOf(::LogTextSettingsViewModel)
    viewModelOf(::CrashHandlerViewerViewModel)
    viewModelOf(::Amp2HtmlSettingsViewModel)
    viewModelOf(::FeatureFlagViewModel)
    viewModelOf(::PretendToBeAppSettingsViewModel)
    viewModelOf(::GeneralSettingsViewModel)
    viewModelOf(::LoadDumpedPreferencesViewModel)
    viewModelOf(::PrivacySettingsViewModel)
    viewModelOf(::ExportSettingsViewModel)
    viewModelOf(::AboutSettingsViewModel)
    viewModelOf(::DevSettingsViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::NotificationSettingsViewModel)
    viewModelOf(::ExperimentsViewModel)
    viewModelOf(::AppConfigViewModel)
    viewModelOf(::ProfileSwitchingSettingsViewModel)
}
