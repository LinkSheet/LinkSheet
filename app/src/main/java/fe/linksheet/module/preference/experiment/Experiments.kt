package fe.linksheet.module.preference.experiment

import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceDefinition
import fe.android.preference.helper.compose.StatePreference

object Experiments : PreferenceDefinition(
    "experiment_drop_categories",
    "experiment_share_allow_custom_share_extras",
    "experiment_share_check_all_extras",
    "experiment_new_query_manager",
    "experiment_url_bar",
    "experiment_url_bar_switch_profile"
) {
    val experiments: List<Experiment>

    val urlPreview = boolean("experiment_url_bar_preview")
    val declutterUrl = boolean("experiment_url_bar_declutter_url")

    val uiOverhaul = boolean("experiment_ui_overhaul")

    val improvedIntentResolver = boolean("experiment_improved_intent_resolver")

    // TODO: Enforce type
    init {
        experiments = listOf(
            Experiment("enhanced_url_bar", hidden = false, urlPreview, declutterUrl),
            Experiment("ui_overhaul", true, uiOverhaul),
            Experiment("improved_intent_resolver", true, improvedIntentResolver)
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

