@file:OptIn(UnsafePreferenceInteraction::class)

package fe.linksheet.module.preference.state

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
        if (experimentsRepository.hasStoredValue(urlBarPreview)) {
            val value = experimentsRepository.raw.unsafeGetBoolean(urlBarPreview, false)
            preferenceRepository.put(AppPreferences.bottomSheet.openGraphPreview.enable, value)
        }

        if (experimentsRepository.hasStoredValue(urlBarPreviewSkipBrowser)) {
            val value = experimentsRepository.raw.unsafeGetBoolean(urlBarPreviewSkipBrowser, false)
            preferenceRepository.put(AppPreferences.bottomSheet.openGraphPreview.skipBrowser, value)
        }
    }
}

object NewDefaults20250803 : AppStateUpdate {
    override fun execute(experimentsRepository: ExperimentRepository) {
        experimentsRepository.put(Experiments.expressiveLoadingSheet, true)
    }
}

class NewDefaults20251215(private val preferenceRepository: AppPreferenceRepository) : AppStateUpdate {
    private val hideReferrerFromSheet = "experiment_hide_referrer_from_sheet"

    override fun execute(experimentsRepository: ExperimentRepository) {
        if (experimentsRepository.hasStoredValue(hideReferrerFromSheet)) {
            val value = experimentsRepository.raw.unsafeGetBoolean(hideReferrerFromSheet, false)
            preferenceRepository.put(AppPreferences.bottomSheet.hideReferringApp, value)
        }
    }
}
