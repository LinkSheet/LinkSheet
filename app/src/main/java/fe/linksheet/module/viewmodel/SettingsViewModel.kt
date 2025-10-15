package fe.linksheet.module.viewmodel


import android.app.Application
import fe.linksheet.module.language.AppLocaleService
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.viewmodel.base.BaseViewModel

class SettingsViewModel(
    val context: Application,
    localeService: AppLocaleService,
    preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository
) : BaseViewModel(preferenceRepository) {

    val currentLocale = localeService.currentLocaleFlow
    val devModeEnabled = preferenceRepository.asViewModelState(AppPreferences.devModeEnabled)
    val newShizuku = experimentRepository.asViewModelState(Experiments.newShizuku)
}
