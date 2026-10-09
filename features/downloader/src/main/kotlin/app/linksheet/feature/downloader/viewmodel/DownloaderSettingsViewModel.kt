package app.linksheet.feature.downloader.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.downloader.preference.DownloaderPreferences

class DownloaderSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository,
    downloaderPreferences: DownloaderPreferences,
) : ViewModel() {

    val enableDownloader = preferenceRepository.asViewModelState(downloaderPreferences.enable)
    val mode = preferenceRepository.asViewModelState(downloaderPreferences.mode)
    val downloaderCheckUrlMimeType = preferenceRepository.asViewModelState(downloaderPreferences.checkUrlMimeType)
    val requestTimeout = preferenceRepository.asViewModelState(downloaderPreferences.requestTimeout)
}
