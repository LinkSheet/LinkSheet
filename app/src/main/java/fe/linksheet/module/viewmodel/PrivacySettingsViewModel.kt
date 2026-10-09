package fe.linksheet.module.viewmodel


import android.app.Application
import androidx.lifecycle.viewModelScope
import app.linksheet.api.SensitivePreference
import app.linksheet.feature.analytics.preference.AnalyticsPreferences
import app.linksheet.feature.analytics.service.BaseAnalyticsService
import app.linksheet.feature.analytics.service.TelemetryLevel
import app.linksheet.feature.remoteconfig.preference.RemoteConfigPreferences
import app.linksheet.api.preference.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.launch

class PrivacySettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository,
    experimentsRepository: ExperimentRepository,
    private val analyticsService: BaseAnalyticsService,
    private val analyticsPreferences: AnalyticsPreferences,
    private val remoteConfigPreferences: RemoteConfigPreferences,
) : BaseViewModel(preferenceRepository) {
    val showAsReferrer = preferenceRepository.asViewModelState(AppPreferences.showLinkSheetAsReferrer)
    val enableAnalytics = experimentsRepository.asViewModelState(Experiments.enableAnalytics)
    val remoteConfig = preferenceRepository.asViewModelState(remoteConfigPreferences.enable)

    @OptIn(SensitivePreference::class)
    val telemetryLevel = preferenceRepository.asViewModelState(analyticsPreferences.telemetryLevel)

    fun updateTelemetryLevel(level: TelemetryLevel) = viewModelScope.launch {
        telemetryLevel(level)
        // TODO: Cancel old job?
        analyticsService.changeLevel(level)
    }

    fun resetIdentifier() {

    }
}
