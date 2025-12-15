@file:OptIn(UnsafePreferenceInteraction::class)

package fe.linksheet.module.preference.state

import fe.android.preference.helper.Preference
import fe.android.preference.helper.UnsafePreferenceInteraction
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments

fun interface AppStateUpdate {
    fun execute(experimentsRepository: ExperimentRepository)
}


object NewDefaults20241216 : AppStateUpdate {
    override fun execute(experimentsRepository: ExperimentRepository) {
        experimentsRepository.put(Experiments.interceptAccidentalTaps, true)
    }
}

class NewDefaults20250729(private val preferenceRepository: AppPreferenceRepository) : AppStateUpdate {
    private val urlBarPreview = "experiment_url_bar_preview"
    private val urlBarPreviewSkipBrowser = "experiment_url_bar_preview_skip_browser"

    override fun execute(experimentsRepository: ExperimentRepository) {
        migrate(experimentsRepository, preferenceRepository, urlBarPreview, AppPreferences.bottomSheet.openGraphPreview.enable)
        migrate(experimentsRepository, preferenceRepository, urlBarPreviewSkipBrowser, AppPreferences.bottomSheet.openGraphPreview.skipBrowser)
    }
}

object NewDefaults20250803 : AppStateUpdate {
    //    private val expressiveLoadingSheet = "experiment_expressive_loading_sheet"
    override fun execute(experimentsRepository: ExperimentRepository) {
//        experimentsRepository.put(expressiveLoadingSheet, true)
    }
}

class NewDefaults20251215(private val preferenceRepository: AppPreferenceRepository) : AppStateUpdate {
    private val hideReferrerFromSheet = "experiment_hide_referrer_from_sheet"
    private val doubleTap = "experiment_impr_btm_sheet_url_double_tap"
    private val expandFully = "experiment_impr_btm_sheet_expand_fully"
    private val autoLaunchSingleBrowser = "experiment_improved_bottom_sheet_auto_launch_single_browser"

    override fun execute(experimentsRepository: ExperimentRepository) {
        migrate(experimentsRepository, preferenceRepository, hideReferrerFromSheet, AppPreferences.bottomSheet.hideReferringApp)
        migrate(experimentsRepository, preferenceRepository, doubleTap, AppPreferences.bottomSheet.doubleTapUrl)
        migrate(experimentsRepository, preferenceRepository, expandFully, AppPreferences.bottomSheet.expandFully)
        migrate(experimentsRepository, preferenceRepository, autoLaunchSingleBrowser, AppPreferences.browserMode.autoLaunchSingleBrowser)
    }
}

private fun migrate(
    experimentsRepository: ExperimentRepository,
    preferenceRepository: AppPreferenceRepository,
    experiment: String,
    preference: Preference.Boolean
) {
    if (experimentsRepository.hasStoredValue(experiment)) {
        val value = experimentsRepository.raw.unsafeGetBoolean(experiment, false)
        preferenceRepository.put(preference, value)
    }
}
