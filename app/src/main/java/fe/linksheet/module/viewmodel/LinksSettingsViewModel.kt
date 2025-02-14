package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.app.AppPreferenceRepository


import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class LinksSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val useClearUrls = preferenceRepository.asState(AppPreferences.useClearUrls)
    val useFastForwardRules = preferenceRepository.asState(AppPreferences.useFastForwardRules)
    val enableLibRedirect = preferenceRepository.asState(AppPreferences.enableLibRedirect)
    val followRedirects = preferenceRepository.asState(AppPreferences.followRedirects)
    val enableDownloader = preferenceRepository.asState(AppPreferences.enableDownloader)
    val enableAmp2Html = preferenceRepository.asState(AppPreferences.enableAmp2Html)
    val resolveEmbeds  = preferenceRepository.asState(AppPreferences.resolveEmbeds)
}
