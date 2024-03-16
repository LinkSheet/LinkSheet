package fe.linksheet.module.viewmodel

import fe.linksheet.module.preference.app.AppPreferenceRepository


import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class ThemeSettingsViewModel(val preferenceRepository: AppPreferenceRepository) : BaseViewModel(preferenceRepository) {
    var theme = preferenceRepository.asState(AppPreferences.theme)
}
