package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.AppPreferenceRepository

import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class LinksSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var useClearUrls = preferenceRepository.getBooleanState(AppPreferences.useClearUrls)
    var useFastForwardRules = preferenceRepository.getBooleanState(AppPreferences.useFastForwardRules)
    var enableLibRedirect = preferenceRepository.getBooleanState(AppPreferences.enableLibRedirect)
    var followRedirects = preferenceRepository.getBooleanState(AppPreferences.followRedirects)
    var enableDownloader = preferenceRepository.getBooleanState(AppPreferences.enableDownloader)
    var enableAmp2Html = preferenceRepository.getBooleanState(AppPreferences.enableAmp2Html)

}