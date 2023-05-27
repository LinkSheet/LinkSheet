package fe.linksheet.module.viewmodel

import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getState
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class ThemeSettingsViewModel(
    val preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var theme = preferenceRepository.getState(Preferences.theme)

}