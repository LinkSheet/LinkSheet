package fe.linksheet.module.viewmodel

import android.app.Application
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class GeneralSettingsViewModel(
    val context: Application,
    preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var disableToasts = preferenceRepository.getBooleanState(Preferences.disableToasts)

}