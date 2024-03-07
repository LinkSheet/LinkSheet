package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.AppPreferenceRepository



import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class DownloaderSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var enableDownloader = preferenceRepository.asState(AppPreferences.enableDownloader)
    var downloaderCheckUrlMimeType = preferenceRepository.asState(
        AppPreferences.downloaderCheckUrlMimeType
    )
    val requestTimeout = preferenceRepository.asState(AppPreferences.requestTimeout)

}
