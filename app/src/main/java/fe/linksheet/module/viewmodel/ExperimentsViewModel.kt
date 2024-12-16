package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import fe.android.preference.helper.compose.StatePreference
import fe.linksheet.navigation.ExperimentSettingsRouteArg
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentGroup
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.viewmodel.base.SavedStateViewModel

class ExperimentsViewModel(
    val context: Application,
    savedStateHandle: SavedStateHandle,
    preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
) : SavedStateViewModel<ExperimentSettingsRouteArg>(savedStateHandle, preferenceRepository) {
    val visibleExperiments = Experiments.experiments.filter { isVisible(it) }

    val stateMap = mutableMapOf<String, StatePreference<Boolean>>().apply {
        for (experiment in visibleExperiments) {
            putAll(experiment.asState(experimentRepository))
        }
    }

    val experiment = getSavedStateFlowNullable(ExperimentSettingsRouteArg::experiment)


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
