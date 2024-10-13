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
    "experiment_url_bar_switch_profile",
    "experiment_ui_overhaul"
) {
    val experiments: List<Experiment>

    val urlPreview = boolean("experiment_url_bar_preview")
    val urlPreviewSkipBrowser = boolean("experiment_url_bar_preview_skip_browser")
    val declutterUrl = boolean("experiment_url_bar_declutter_url")

    val improvedIntentResolver = boolean("experiment_improved_intent_resolver", true)
    val improvedBottomSheetExpandFully = boolean("experiment_impr_btm_sheet_expand_fully")
    val improvedBottomSheetUrlDoubleTap = boolean("experiment_impr_btm_sheet_url_double_tap")

    val libRedirectJsEngine = boolean("experiment_enable_libredirect_js_engine")

    val enableAnalytics = boolean("experiment_enable_analytics", false)

    val editClipboard = boolean("experiment_edit_clipboard", false)

    // TODO: Enforce type
    init {
        enableAnalytics.migrate { repository, _ -> repository.put(enableAnalytics, false) }
        improvedIntentResolver.migrate { repository, _ ->
            // Used to be false, if user has not manually changed this, migrate to true; If they turn it back off, we won't update it again
            if (!repository.hasStoredValue(improvedIntentResolver)) {
                repository.put(improvedIntentResolver, true)
            }
        }

        experiments = listOf(
            Experiment(
                name = "enhanced_url_bar",
                preferences = arrayOf(urlPreview, urlPreviewSkipBrowser, declutterUrl)
            ),
            //Experiment("ui_overhaul", true, uiOverhaul),
            Experiment(
                "improved_bottom_sheet",
                preferences = arrayOf(
                    improvedIntentResolver,
                    improvedBottomSheetExpandFully,
                    improvedBottomSheetUrlDoubleTap,
                    libRedirectJsEngine
                )
            ),
            Experiment(
                name= "edit_clipboard",
                displayName = "Edit clipboard content on home page",
                preferences = arrayOf(editClipboard)
            )
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

class Experiment(
    val name: String,
    val displayName: String = "Experiment $name",
    vararg val preferences: Preference.Boolean,
) {
    val hidden: Boolean = false
    val defaultValues: Map<String, Boolean> by lazy { preferences.associate { it.key to it.default } }

    fun asState(repository: ExperimentRepository): Map<String, StatePreference<Boolean>> {
        return preferences.associate { preference -> preference.key to repository.asState(preference) }
    }

    fun isVisible(repository: ExperimentRepository): Boolean {
        return !hidden || repository.hasExperiment(defaultValues.keys)
    }
}

