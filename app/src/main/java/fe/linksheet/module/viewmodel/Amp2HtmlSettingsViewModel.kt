package fe.linksheet.module.viewmodel

import android.app.Application
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.android.preference.helper.compose.getIntState
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class Amp2HtmlSettingsViewModel(
    val context: Application,
    preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var enableAmp2Html = preferenceRepository.getBooleanState(Preferences.enableAmp2Html)
    val enableAmp2HtmlLocalCache = preferenceRepository.getBooleanState(
        Preferences.amp2HtmlLocalCache
    )

    val amp2HtmlExternalService = preferenceRepository.getBooleanState(
        Preferences.amp2HtmlExternalService
    )
    val requestTimeout = preferenceRepository.getIntState(Preferences.requestTimeout)
}