package fe.linksheet.module.viewmodel

import android.app.Application
import fe.android.preference.helper.compose.StatePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.Experiment
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.viewmodel.base.BaseViewModel

class ExperimentsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
) : BaseViewModel(preferenceRepository) {
    val states = mutableMapOf<String, StatePreference<Boolean>>()

    init {
        for (experiment in Experiments.experiments) {
            states.putAll(experiment.asState(experimentRepository))
        }
    }

    fun isVisible(experiment: Experiment): Boolean {
        // Stub this for now
        return true
    }

    fun resetAll() {
        for (experiment in Experiments.experiments) {
            for ((key, def) in experiment.defaultValues) {
                states[key]!!.invoke(def)
            }
        }
    }
}
