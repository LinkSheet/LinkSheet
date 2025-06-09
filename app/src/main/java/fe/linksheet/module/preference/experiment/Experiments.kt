package fe.linksheet.module.preference.experiment

import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceDefinition
import fe.composekit.preference.ViewModelStatePreference

object Experiments : PreferenceDefinition(
    "experiment_drop_categories",
    "experiment_share_allow_custom_share_extras",
    "experiment_share_check_all_extras",
    "experiment_new_query_manager",
    "experiment_url_bar",
    "experiment_url_bar_switch_profile",
    "experiment_ui_overhaul",
    "experiment_edit_clipboard"
) {
    val urlPreview = boolean(
        key = "experiment_url_bar_preview"
    )
    val urlPreviewSkipBrowser = boolean(
        key = "experiment_url_bar_preview_skip_browser"
    )

    val improvedBottomSheetExpandFully = boolean(
        key = "experiment_impr_btm_sheet_expand_fully"
    )
    val improvedBottomSheetUrlDoubleTap = boolean(
        key = "experiment_impr_btm_sheet_url_double_tap"
    )
    val autoLaunchSingleBrowser = boolean(
        key = "experiment_improved_bottom_sheet_auto_launch_single_browser"
    )
    val interceptAccidentalTaps = boolean(
        key = "experiment_intercept_accidental_taps",
        default = true
    )
    val manualFollowRedirects = boolean(
        key = "experiment_manual_follow_redirects",
        default = false
    )

    val libRedirectJsEngine = boolean(
        key = "experiment_enable_libredirect_js_engine"
    )

    val enableAnalytics = boolean(
        key = "experiment_enable_analytics",
        default = false
    )
    val hideReferrerFromSheet = boolean(
        key = "experiment_hide_referrer_from_sheet"
    )

    val noBottomSheetStateSave = boolean(
        key = "experiment_no_bottom_sheet_state_save"
    )
    val aggressiveFollowRedirects = boolean(
        key = "experiment_aggressive_follow_redirects"
    )
    val expressiveLoadingSheet = boolean(
        key = "experiment_expressive_loading_sheet"
    )

    val disableLogging = boolean(
        key = "experiment_disable_log_persistence"
    )

    val newVlh = boolean(
        key = "experiment_new_vlh",
    )

    val experiments = listOf(
        group(
            name = "enhanced_url_bar",
            displayName = "Enhanced url bar",
            experiment("Open Graph preview", urlPreview),
            experiment("Disable preview if referrer is browser", urlPreviewSkipBrowser)
        ),
        group(
            name = "improved_bottom_sheet",
            displayName = "Improved bottom sheet",
            experiment("Auto-expand bottom sheet fully", improvedBottomSheetExpandFully),
            experiment("Double tap url to open app", improvedBottomSheetUrlDoubleTap),
            experiment("LibRedirect QuickJS engine", libRedirectJsEngine),
            experiment("Hide referring app from results in bottom sheet", hideReferrerFromSheet),
            experiment("Ignore accidental taps while sheet is animating", interceptAccidentalTaps),
            experiment("Auto-launch single browser", autoLaunchSingleBrowser),
            experiment("Manual redirect resolving", manualFollowRedirects),
            experiment("Disable bottom sheet state save", noBottomSheetStateSave),
            experiment("Aggressive follow redirects", aggressiveFollowRedirects),
            experiment("Expressive loading indicator", expressiveLoadingSheet)
        ),
        group(
            name = "logging",
            displayName = "Logging",
            experiment("Disable log persistence", disableLogging)
        ),
        group(
            name = "new_vlh",
            displayName = "New verified link handlers page",
            experiment("Enable new VLH page", newVlh)
        )
    )

    // TODO: Enforce type
    init {
        enableAnalytics.migrate { repository, _ -> repository.put(enableAnalytics, false) }
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

private fun group(name: String, displayName: String, vararg experiments: ExperimentPreference): ExperimentGroup {
    val group = ExperimentGroup(name, displayName)
    for (preference in experiments) {
        group.addPreference(preference)
    }

    return group
}

private fun experiment(displayName: String, experiment: Preference.Boolean): ExperimentPreference {
    return ExperimentPreference(displayName, experiment)
}


class ExperimentGroup(val name: String, val displayName: String = "Experiment $name") {
    private val _preferences = mutableListOf<ExperimentPreference>()
    val preferences: List<ExperimentPreference> = _preferences

    fun addPreference(preference: ExperimentPreference) {
        _preferences.add(preference)
    }

    val defaultValues: Map<String, Boolean> by lazy { _preferences.associate { it.preference.key to it.preference.default } }

    fun asState(repository: ExperimentRepository): Map<String, ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>> {
        return _preferences.associate { preference ->
            preference.preference.key to repository.asViewModelState(
                preference.preference
            )
        }
    }

    val hidden: Boolean = false
    fun isVisible(repository: ExperimentRepository): Boolean {
        return !hidden || repository.hasExperiment(defaultValues.keys)
    }
}

data class ExperimentPreference(val displayName: String, val preference: Preference.Boolean)
