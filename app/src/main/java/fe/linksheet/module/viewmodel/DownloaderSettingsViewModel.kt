package fe.linksheet.module.viewmodel

import android.app.Application
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.android.preference.helper.compose.getIntState
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class DownloaderSettingsViewModel(
    val context: Application,
    preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var enableDownloader = preferenceRepository.getBooleanState(Preferences.enableDownloader)
    var downloaderCheckUrlMimeType = preferenceRepository.getBooleanState(
        Preferences.downloaderCheckUrlMimeType
    )
    val requestTimeout = preferenceRepository.getIntState(Preferences.requestTimeout)

}