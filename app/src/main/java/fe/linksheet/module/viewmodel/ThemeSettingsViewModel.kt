package fe.linksheet.module.viewmodel

import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel

class ThemeSettingsViewModel(
    val preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
) : BaseViewModel(preferenceRepository) {
    val themeV2 = preferenceRepository.asState(AppPreferences.themeV2)

    val themeMaterialYou = preferenceRepository.asState(AppPreferences.themeMaterialYou)
    val themeAmoled = preferenceRepository.asState(AppPreferences.themeAmoled)
}
