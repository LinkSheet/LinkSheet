package fe.linksheet.module.viewmodel

import android.app.Application
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class LinksSettingsViewModel(
    val context: Application,
    preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var useClearUrls = preferenceRepository.getBooleanState(Preferences.useClearUrls)
    var useFastForwardRules = preferenceRepository.getBooleanState(Preferences.useFastForwardRules)
    var enableLibRedirect = preferenceRepository.getBooleanState(Preferences.enableLibRedirect)
    var followRedirects = preferenceRepository.getBooleanState(Preferences.followRedirects)
    var enableDownloader = preferenceRepository.getBooleanState(Preferences.enableDownloader)
    var downloaderCheckUrlMimeType =
        preferenceRepository.getBooleanState(Preferences.downloaderCheckUrlMimeType)
}