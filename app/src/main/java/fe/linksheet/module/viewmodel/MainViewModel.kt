package fe.linksheet.module.viewmodel


import android.app.Activity
import android.app.Application
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import app.linksheet.api.SensitivePreference
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.compose.debug.DebugMenuSlotProvider
import app.linksheet.feature.analytics.preference.AnalyticsPreferences
import app.linksheet.feature.analytics.service.AnalyticsEvent
import app.linksheet.feature.analytics.service.BaseAnalyticsService
import app.linksheet.feature.analytics.service.TelemetryLevel
import app.linksheet.feature.app.core.PackageIntentHandler
import app.linksheet.feature.devicecompat.miui.MiuiCompat
import app.linksheet.feature.devicecompat.miui.MiuiCompatProvider
import app.linksheet.feature.remoteconfig.preference.RemoteConfigPreferences
import dev.zwander.shared.ShizukuUtil
import fe.composekit.extension.getSystemServiceOrThrow
import fe.linksheet.module.ClipboardUseCase
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.state.AppStatePreferences
import fe.linksheet.module.preference.state.AppStateRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.extension.android.tryStartActivity
import fe.std.coroutines.RefreshableStateFlow
import fe.std.result.isSuccess
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch


class MainViewModel(
    val context: Application,
    val appStateRepository: AppStateRepository,
    val preferenceRepository: AppPreferenceRepository,
    val experimentRepository: ExperimentRepository,
    private val remoteConfigPreferences: RemoteConfigPreferences,
    private val analyticsPreferences: AnalyticsPreferences,
    private val analyticsService: BaseAnalyticsService,
    private val miuiCompatProvider: MiuiCompatProvider,
    private val miuiCompat: MiuiCompat,
    val debugMenu: DebugMenuSlotProvider,
    private val intentHandler: PackageIntentHandler,
) : BaseViewModel(preferenceRepository) {
    val clipboardUseCase: ClipboardUseCase = ClipboardUseCase(
        repository = preferenceRepository,
        clipboardManager = context.getSystemServiceOrThrow<ClipboardManager>(),
        coroutineScope = viewModelScope
    )

    init {
        Log.d("MainViewModel", "init")
        clipboardUseCase.init()
        addCloseable(clipboardUseCase)
    }

    val newDefaultsDismissed =
        appStateRepository.asViewModelState(AppStatePreferences.newDefaults_2025_12_15_InfoDismissed)

    @OptIn(SensitivePreference::class)
    val telemetryLevel = preferenceRepository.asViewModelState(analyticsPreferences.telemetryLevel)
    val telemetryShowInfoDialog =
        preferenceRepository.asViewModelState(analyticsPreferences.telemetryShowInfoDialog)
    val remoteConfigDialogDismissed = appStateRepository.asViewModelState(AppStatePreferences.remoteConfigDialogDismissed)
    val remoteConfig = preferenceRepository.asViewModelState(remoteConfigPreferences.enable)

    private val _shizukuRunning = RefreshableStateFlow(false) {
        ShizukuUtil.isShizukuRunning()
    }

    val shizukuRunning = _shizukuRunning

    private val _shizukuInstalled = RefreshableStateFlow(false) {
        ShizukuUtil.isShizukuInstalled(context)
    }

    val shizukuInstalled = _shizukuInstalled

    private val _showMiuiAlert = RefreshableStateFlow(false) {
        if (miuiCompatProvider.isRequired.value) miuiCompat.showAlert(context) else false
    }

    val showMiuiAlert = _showMiuiAlert

    suspend fun updateMiuiAutoStartAppOp(activity: Activity?): Boolean {
        if (activity == null) return false
        val result = miuiCompat.startPermissionRequest(activity)
        _showMiuiAlert.refresh()

        return result
    }

    private val _defaultBrowser = { intentHandler.isSelfDefaultBrowser() }.asFlow()
    val defaultBrowser = _defaultBrowser

    fun launchIntent(activity: Activity?, intent: SettingsIntent): Boolean {
        if (activity == null) return false
        return activity.tryStartActivity(Intent(intent.action)).isSuccess()
    }

    fun enqueueNavEvent(destination: NavDestination, args: Bundle?) = viewModelScope.launch {
        analyticsService.enqueue(AnalyticsEvent.Navigate(destination.route ?: "<no_route>"))
    }

    fun updateTelemetryLevel(level: TelemetryLevel) = viewModelScope.launch {
        telemetryLevel(level)
        telemetryShowInfoDialog(false)
        analyticsService.changeLevel(level)
    }

    fun setRemoteConfig(enabled: Boolean) {
        remoteConfigDialogDismissed(true)
        remoteConfig(enabled)
    }

    enum class SettingsIntent(val action: String) {
        DefaultApps(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
        DomainUrls("android.settings.MANAGE_DOMAIN_URLS"),
        CrossProfileAccess("android.settings.MANAGE_CROSS_PROFILE_ACCESS")
    }
}

