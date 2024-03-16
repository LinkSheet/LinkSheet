package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.app.AppPreferenceRepository


import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class Amp2HtmlSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var enableAmp2Html = preferenceRepository.asState(AppPreferences.enableAmp2Html)
    val enableAmp2HtmlLocalCache = preferenceRepository.asState(
        AppPreferences.amp2HtmlLocalCache
    )
    val amp2HtmlBuiltInCache = preferenceRepository.asState(
        AppPreferences.amp2HtmlBuiltInCache
    )
    val amp2HtmlExternalService = preferenceRepository.asState(
        AppPreferences.amp2HtmlExternalService
    )
    val amp2HtmlAllowDarknets = preferenceRepository.asState(
        AppPreferences.amp2HtmlAllowDarknets
    )
    val requestTimeout = preferenceRepository.asState(AppPreferences.requestTimeout)
}
