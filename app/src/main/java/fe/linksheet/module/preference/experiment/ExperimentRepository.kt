package fe.linksheet.module.preference.experiment

import android.content.Context
import fe.android.preference.helper.compose.StatePreferenceRepository

class ExperimentRepository(val context: Context) : StatePreferenceRepository(context, "experiments") {
    init {
        Experiments.runMigrations(this)
    }

    fun hasExperiment(keys: Set<String>): Boolean {
        for (key in keys) {
            if (hasStoredValue(key)) return true
        }
        return false
    }
}
