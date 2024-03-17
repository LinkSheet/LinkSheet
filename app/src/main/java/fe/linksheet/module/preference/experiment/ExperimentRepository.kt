package fe.linksheet.module.preference.experiment

import android.content.Context
import fe.android.preference.helper.compose.StatePreferenceRepository

class ExperimentRepository(val context: Context) : StatePreferenceRepository(context, "experiments") {
    // Hack around repo until we have a contains() api
    private val prefs = context.getSharedPreferences(context.packageName + "_experiments", Context.MODE_PRIVATE)

    fun hasExperiment(keys: Set<String>): Boolean {
        for (key in keys) if (key in prefs) return true
        return false
    }
}
