package fe.linksheet.module.preference.experiment

import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceDefinition
import fe.android.preference.helper.compose.StatePreference

object Experiments : PreferenceDefinition() {
    val experiments: List<Experiment>

    val experimentalUrlBar = boolean("experiment_url_bar")
    val urlPreview = boolean("experiment_url_bar_preview")
    val declutterUrl = boolean("experiment_url_bar_declutter_url")
    val switchProfile = boolean("experiment_url_bar_switch_profile")

    val allowCustomShareExtras = boolean("experiment_share_allow_custom_share_extras")
    val checkAllExtras = boolean("experiment_share_check_all_extras")

    val newQueryManager = boolean("experiment_new_query_manager")
    val uiOverhaul = boolean("experiment_ui_overhaul")

    // TODO: Enforce type
    init {
        experiments = listOf(
            Experiment("enhanced_url_bar", hidden = false, experimentalUrlBar, urlPreview, declutterUrl, switchProfile),
            Experiment("share_to", hidden = false, allowCustomShareExtras, checkAllExtras),
            Experiment("new_query_manager", true, newQueryManager),
            Experiment("ui_overhaul", true, uiOverhaul),
        )

        finalize()
    }

    fun getActive(repository: ExperimentRepository): List<String> {
        val active = mutableListOf<String>()

        for ((key, pref) in all) {
            if (pref is Preference.Boolean && repository.get(pref)) active.add(key)
        }

        return active
    }
}

class Experiment(val name: String, val hidden: Boolean = false, vararg val preferences: Preference.Boolean) {
    val defaultValues: Map<String, Boolean> by lazy { preferences.associate { it.key to it.default } }

    fun asState(repository: ExperimentRepository): Map<String, StatePreference<Boolean>> {
        return preferences.associate { preference -> preference.key to repository.asState(preference) }
    }

    fun isVisible(repository: ExperimentRepository): Boolean {
        return !hidden || repository.hasExperiment(defaultValues.keys)
    }
}

