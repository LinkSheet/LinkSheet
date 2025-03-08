package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import fe.linksheet.module.preference.app.AppPreferenceRepository



import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class DownloaderSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val enableDownloader = preferenceRepository.asViewModelState(AppPreferences.enableDownloader)
    val downloaderCheckUrlMimeType = preferenceRepository.asViewModelState(AppPreferences.downloaderCheckUrlMimeType)
    val requestTimeout = preferenceRepository.asViewModelState(AppPreferences.requestTimeout)

}
