package fe.linksheet.module.viewmodel

import fe.linksheet.module.preference.app.AppPreferenceRepository


import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.viewmodel.base.BaseViewModel

class ThemeSettingsViewModel(
    val preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
) : BaseViewModel(preferenceRepository) {
    var theme = preferenceRepository.asState(AppPreferences.theme)
    val themeMaterialYou = preferenceRepository.asState(AppPreferences.themeMaterialYou)
    var themeAmoled = preferenceRepository.asState(AppPreferences.themeAmoled)

    val uiOverhaul = experimentRepository.asState(Experiments.uiOverhaul)
}
