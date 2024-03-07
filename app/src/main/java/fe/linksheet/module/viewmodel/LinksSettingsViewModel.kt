package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.AppPreferenceRepository


import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class LinksSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var useClearUrls = preferenceRepository.asState(AppPreferences.useClearUrls)
    var useFastForwardRules = preferenceRepository.asState(AppPreferences.useFastForwardRules)
    var enableLibRedirect = preferenceRepository.asState(AppPreferences.enableLibRedirect)
    var followRedirects = preferenceRepository.asState(AppPreferences.followRedirects)
    var enableDownloader = preferenceRepository.asState(AppPreferences.enableDownloader)
    var enableAmp2Html = preferenceRepository.asState(AppPreferences.enableAmp2Html)
    val resolveEmbeds  = preferenceRepository.asState(AppPreferences.resolveEmbeds)
}
