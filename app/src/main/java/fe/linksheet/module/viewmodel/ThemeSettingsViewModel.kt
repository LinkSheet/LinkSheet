package fe.linksheet.module.viewmodel

import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class ThemeSettingsViewModel(
    val preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var theme = preferenceRepository.getState(Preferences.theme)

}