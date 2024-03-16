package fe.linksheet.module.preference.experiment

import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceDefinition
import fe.android.preference.helper.compose.StatePreference
import fe.android.preference.helper.compose.mock.MockRepositoryState

object Experiments : PreferenceDefinition() {
    val experiments: List<Experiment>

    val experimentalUrlBar = boolean("experiment_url_bar")
    val urlPreview = boolean("experiment_url_bar_preview")
    val declutterUrl = boolean("experiment_url_bar_declutter_url")

    val allowCustomShareExtras = boolean("experiment_share_allow_custom_share_extras")
    val checkAllExtras = boolean("experiment_share_check_all_extras")

    // TODO: Enforce type
    init {
        experiments = listOf(
            Experiment("enhanced_url_bar", experimentalUrlBar, urlPreview, declutterUrl),
            Experiment("share_to", allowCustomShareExtras, checkAllExtras)
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

class Experiment(val name: String, vararg val preferences: Preference.Boolean) {
    val defaultValues: Map<String, Boolean> by lazy { preferences.associate { it.key to it.default } }

    fun asState(repository: ExperimentRepository): Map<String, StatePreference<Boolean>> {
        return preferences.associate { preference -> preference.key to repository.asState(preference) }
    }
}

