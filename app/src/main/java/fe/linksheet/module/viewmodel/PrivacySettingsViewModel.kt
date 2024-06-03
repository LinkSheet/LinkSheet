package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.analytics.AnalyticsClient
import fe.linksheet.module.analytics.TelemetryLevel
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository


import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.viewmodel.base.BaseViewModel

class PrivacySettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository,
    experimentsRepository: ExperimentRepository,
    private val analyticsClient: AnalyticsClient,
) : BaseViewModel(preferenceRepository) {
    var showAsReferrer = preferenceRepository.asState(AppPreferences.showLinkSheetAsReferrer)
    val enableAnalytics = experimentsRepository.asState(Experiments.enableAnalytics)

    @OptIn(SensitivePreference::class)
    val telemetryLevel = preferenceRepository.asState(AppPreferences.telemetryLevel)

    fun updateTelemetryLevel(level: TelemetryLevel) {
        telemetryLevel(level)
        // TODO: Cancel old job?
        analyticsClient.updateLevel(level).tryStart()
    }

    fun resetIdentifier() {

    }
}
