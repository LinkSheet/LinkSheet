package app.linksheet.feature.downloader

import app.linksheet.feature.downloader.core.Downloader
import app.linksheet.feature.downloader.viewmodel.DownloaderSettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val DownloaderFeatureModule = module {
    single<Downloader> {
        Downloader(client = get())
    }
    viewModelOf(::DownloaderSettingsViewModel)
}
