package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import fe.android.preference.helper.Preference
import fe.android.preference.helper.compose.StatePreference
import fe.composekit.preference.ViewModelStatePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentGroup
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.viewmodel.base.BaseViewModel

class ExperimentsViewModel(
    val context: Application,
    savedStateHandle: SavedStateHandle,
    preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
) : BaseViewModel(preferenceRepository) {
    val visibleExperiments = Experiments.experiments.filter { isVisible(it) }

    val stateMap by lazy {
        mutableMapOf<String, ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>>().apply {
            for (experiment in visibleExperiments) {
                putAll(experiment.asState(experimentRepository))
            }
        }
    }

    fun isVisible(experiment: ExperimentGroup): Boolean {
        // Stub this for now
        return true
    }

    fun resetAll() {
        for (experiment in visibleExperiments) {
            for ((key, def) in experiment.defaultValues) {
                stateMap[key]!!.invoke(def)
            }
        }
    }
}
