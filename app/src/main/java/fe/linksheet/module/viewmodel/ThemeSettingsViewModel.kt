package fe.linksheet.module.viewmodel

import fe.linksheet.module.preference.AppPreferenceRepository

import fe.android.preference.helper.compose.getState
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class ThemeSettingsViewModel(val preferenceRepository: AppPreferenceRepository) : BaseViewModel(preferenceRepository) {
    var theme = preferenceRepository.getState(AppPreferences.theme)
}
