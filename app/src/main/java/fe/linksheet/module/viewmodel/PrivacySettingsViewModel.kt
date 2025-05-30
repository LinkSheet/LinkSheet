package fe.linksheet.module.viewmodel


import android.app.Application
import androidx.lifecycle.viewModelScope
import fe.linksheet.module.analytics.BaseAnalyticsService
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
    private val analyticsService: BaseAnalyticsService,
) : BaseViewModel(preferenceRepository) {
    val showAsReferrer = preferenceRepository.asViewModelState(AppPreferences.showLinkSheetAsReferrer)
    val enableAnalytics = experimentsRepository.asViewModelState(Experiments.enableAnalytics)

    @OptIn(SensitivePreference::class)
    val telemetryLevel = preferenceRepository.asViewModelState(AppPreferences.telemetryLevel)

    fun updateTelemetryLevel(level: TelemetryLevel) {
        telemetryLevel(level)
        // TODO: Cancel old job?
        analyticsService.changeLevel(level)
    }

    fun resetIdentifier() {

    }
}
