package fe.linksheet.module.viewmodel

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::AppsWhichCanOpenLinksViewModel)
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
}