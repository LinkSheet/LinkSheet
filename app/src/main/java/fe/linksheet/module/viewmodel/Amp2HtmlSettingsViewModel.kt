package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.app.AppPreferenceRepository


import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class Amp2HtmlSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val enableAmp2Html = preferenceRepository.asViewModelState(AppPreferences.amp2Html.enable)
    val enableAmp2HtmlLocalCache = preferenceRepository.asViewModelState(AppPreferences.amp2Html.localCache)
    val amp2HtmlExternalService = preferenceRepository.asViewModelState(AppPreferences.amp2Html.externalService)
    val amp2HtmlAllowDarknets = preferenceRepository.asViewModelState(AppPreferences.amp2Html.allowDarknets)
    val amp2HtmlAllowLocalNetwork = preferenceRepository.asViewModelState(AppPreferences.amp2Html.allowLocalNetwork)
    val amp2HtmlSkipBrowser = preferenceRepository.asViewModelState(AppPreferences.amp2Html.skipBrowser)
    val requestTimeout = preferenceRepository.asViewModelState(AppPreferences.requestTimeout)
}
