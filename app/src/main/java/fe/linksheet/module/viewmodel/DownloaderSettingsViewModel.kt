package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.app.AppPreferenceRepository



import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class DownloaderSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val enableDownloader = preferenceRepository.asState(AppPreferences.enableDownloader)
    val downloaderCheckUrlMimeType = preferenceRepository.asState(
        AppPreferences.downloaderCheckUrlMimeType
    )
    val requestTimeout = preferenceRepository.asState(AppPreferences.requestTimeout)

}
