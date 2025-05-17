package fe.linksheet.module.viewmodel


import android.app.Activity
import android.app.Application
import android.app.role.RoleManager
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.getSystemService
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import fe.composekit.core.AndroidVersion
import fe.linksheet.BuildConfig
import fe.linksheet.R
import fe.linksheet.extension.android.getFirstText
import fe.linksheet.extension.android.resolveActivityCompat
import fe.linksheet.extension.android.setText
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.extension.kotlinx.RefreshableStateFlow
import fe.linksheet.module.analytics.AnalyticsEvent
import fe.linksheet.module.analytics.BaseAnalyticsService
import fe.linksheet.module.analytics.TelemetryLevel
import fe.linksheet.module.app.`package`.PackageIntentHandler
import fe.linksheet.module.debug.DebugMenuSlotProvider
import fe.linksheet.module.devicecompat.miui.MiuiCompat
import fe.linksheet.module.devicecompat.miui.MiuiCompatProvider
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import fe.linksheet.module.preference.state.AppStatePreferences
import fe.linksheet.module.preference.state.AppStateRepository
import fe.linksheet.module.resolver.BrowserResolver
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.web.UriUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow


class MainViewModel(
    val context: Application,
    val appStateRepository: AppStateRepository,
    val preferenceRepository: AppPreferenceRepository,
    val experimentRepository: ExperimentRepository,
    val browserResolver: BrowserResolver,
    featureFlagRepository: FeatureFlagRepository,
    private val analyticsService: BaseAnalyticsService,
    private val miuiCompatProvider: MiuiCompatProvider,
    private val miuiCompat: MiuiCompat,
    val debugMenu: DebugMenuSlotProvider,
    private val intentHandler: PackageIntentHandler,
) : BaseViewModel(preferenceRepository) {
    val newDefaultsDismissed = appStateRepository.asViewModelState(AppStatePreferences.newDefaults_2024_12_29_InfoDismissed)

    @OptIn(SensitivePreference::class)
    val telemetryLevel = experimentRepository.asViewModelState(AppPreferences.telemetryLevel)

    val telemetryShowInfoDialog = experimentRepository.asViewModelState(AppPreferences.telemetryShowInfoDialog)

    val editClipboard = experimentRepository.asViewModelState(Experiments.editClipboard)
    val homeClipboardCard = experimentRepository.asViewModelState(AppPreferences.homeClipboardCard)

    private val roleManager by lazy {
        if (AndroidVersion.isAtLeastApi26O()) {
            context.getSystemService<RoleManager>()
        } else null
    }

    private val clipboardManager by lazy { context.getSystemService<ClipboardManager>()!! }

    private val _clipboardContent = MutableStateFlow<Uri?>(null)
    val clipboardContent = _clipboardContent.asStateFlow()

    fun tryReadClipboard() {
        if (!homeClipboardCard.value) {
            _clipboardContent.tryEmit(null)
            return
        }

        val clipboardUri = clipboardManager.getFirstText()?.let { tryParseUriString(it) }
        if (clipboardUri != null && _clipboardContent.value != clipboardUri) {
            _clipboardContent.tryEmit(clipboardUri)
        }
    }

    fun tryUpdateClipboard(label: String, uriStr: String) {
        val uri = tryParseUriString(uriStr)
        if (uri != null) {
            clipboardManager.setText(label, uri.toString())
        }
    }

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
        return activity.startActivityWithConfirmation(Intent(intent.action))
    }

    private fun tryParseUriString(uriStr: String): Uri? {
        return UriUtil.parseWebUriStrict(uriStr)
    }

    fun enqueueNavEvent(destination: NavDestination, args: Bundle?) {
        analyticsService.enqueue(AnalyticsEvent.Navigate(destination.route ?: "<no_route>"))
    }

    fun updateTelemetryLevel(level: TelemetryLevel) {
        telemetryLevel.update(level)
        telemetryShowInfoDialog.update(false)
        analyticsService.changeLevel(level)
    }


    enum class SettingsIntent(val action: String) {
        DefaultApps(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
        DomainUrls("android.settings.MANAGE_DOMAIN_URLS"),
        CrossProfileAccess("android.settings.MANAGE_CROSS_PROFILE_ACCESS")
    }
}
