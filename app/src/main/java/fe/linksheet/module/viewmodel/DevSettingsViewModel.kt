package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.AppPreferenceRepository

import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class DevSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var devModeEnabled = preferenceRepository.getBooleanState(AppPreferences.devModeEnabled)
    val useDevBottomSheet = preferenceRepository.getBooleanState(AppPreferences.useDevBottomSheet)
    val devBottomSheetExperiment = preferenceRepository.getBooleanState(AppPreferences.devBottomSheetExperiment)
}
