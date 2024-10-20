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
    val experiments: List<ExperimentGroup>

    val urlPreview = boolean("experiment_url_bar_preview")
    val urlPreviewSkipBrowser = boolean("experiment_url_bar_preview_skip_browser")
    val declutterUrl = boolean("experiment_url_bar_declutter_url")

    val improvedIntentResolver = boolean("experiment_improved_intent_resolver", true)
    val improvedBottomSheetExpandFully = boolean("experiment_impr_btm_sheet_expand_fully")
    val improvedBottomSheetUrlDoubleTap = boolean("experiment_impr_btm_sheet_url_double_tap")

    val libRedirectJsEngine = boolean("experiment_enable_libredirect_js_engine")

    val enableAnalytics = boolean("experiment_enable_analytics", false)

    val editClipboard = boolean("experiment_edit_clipboard", false)
    val hideReferrerFromSheet = boolean("experiment_hide_referrer_from_sheet")


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
            ExperimentGroup("enhanced_url_bar", "Enhanced url bar").apply {
                addPreference(ExperimentPreference("Open Graph preview", urlPreview))
                addPreference(ExperimentPreference("Disable preview if referrer is browser", urlPreviewSkipBrowser))
                addPreference(ExperimentPreference("Declutter url", declutterUrl))
            },

            ExperimentGroup("improved_bottom_sheet", "Improved bottom sheet").apply {
                addPreference(ExperimentPreference("Improved intent resolver", improvedIntentResolver))
                addPreference(ExperimentPreference("Auto-expand bottom sheet fully", improvedBottomSheetExpandFully))
                addPreference(ExperimentPreference("Double tap url to open app", improvedBottomSheetUrlDoubleTap))
                addPreference(ExperimentPreference("LibRedirect QuickJS engine", libRedirectJsEngine))
                addPreference(ExperimentPreference("Hide referring app from results in bottom sheet", hideReferrerFromSheet))
            },

            ExperimentGroup("edit_clipboard", "Edit clipboard content on home page").apply {
                addPreference(ExperimentPreference("Enable", editClipboard))
            },
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

class ExperimentGroup(val name: String, val displayName: String = "Experiment $name") {
    private val _preferences = mutableListOf<ExperimentPreference>()
    val preferences: List<ExperimentPreference> = _preferences

    fun addPreference(preference: ExperimentPreference) {
        _preferences.add(preference)
    }

    val defaultValues: Map<String, Boolean> by lazy { _preferences.associate { it.preference.key to it.preference.default } }

    fun asState(repository: ExperimentRepository): Map<String, StatePreference<Boolean>> {
        return _preferences.associate { preference -> preference.preference.key to repository.asState(preference.preference) }
    }

    val hidden: Boolean = false
    fun isVisible(repository: ExperimentRepository): Boolean {
        return !hidden || repository.hasExperiment(defaultValues.keys)
    }
}

data class ExperimentPreference(val displayName: String, val preference: Preference.Boolean)
