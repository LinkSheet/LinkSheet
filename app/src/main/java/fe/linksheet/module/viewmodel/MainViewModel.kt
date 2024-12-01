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
import fe.android.compose.version.AndroidVersion
import fe.linksheet.BuildConfig
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.R
import fe.linksheet.extension.android.getFirstText
import fe.linksheet.extension.android.resolveActivityCompat
import fe.linksheet.extension.android.setText
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.extension.kotlinx.RefreshableStateFlow
import fe.linksheet.module.analytics.AnalyticsEvent
import fe.linksheet.module.analytics.BaseAnalyticsService
import fe.linksheet.module.analytics.TelemetryLevel
import fe.linksheet.module.devicecompat.MiuiCompat
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import fe.linksheet.module.resolver.BrowserResolver
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.UriUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration


class MainViewModel(
    val context: Application,
    val preferenceRepository: AppPreferenceRepository,
    val experimentRepository: ExperimentRepository,
    val browserResolver: BrowserResolver,
    featureFlagRepository: FeatureFlagRepository,
    private val analyticsService: BaseAnalyticsService,
    private val miuiCompat: MiuiCompat,
) : BaseViewModel(preferenceRepository) {

    val firstRun = preferenceRepository.asState(AppPreferences.firstRun)

    @OptIn(SensitivePreference::class)
    val useTimeMs = preferenceRepository.get(AppPreferences.useTimeMs)
    val showDiscordBanner = preferenceRepository.asState(AppPreferences.showDiscordBanner)
    val donateCardDismissed = preferenceRepository.asState(AppPreferences.donateCardDismissed)
    var themeV2 = preferenceRepository.asState(AppPreferences.themeV2)

    @OptIn(SensitivePreference::class)
    val telemetryLevel = preferenceRepository.asState(AppPreferences.telemetryLevel)

    val telemetryShowInfoDialog = preferenceRepository.asState(AppPreferences.telemetryShowInfoDialog)

    val editClipboard = experimentRepository.asState(Experiments.editClipboard)

    private val roleManager by lazy {
        if (AndroidVersion.AT_LEAST_API_26_O) {
            context.getSystemService<RoleManager>()
        } else null
    }

    private val clipboardManager by lazy { context.getSystemService<ClipboardManager>()!! }

    private val _clipboardContent = MutableStateFlow<Uri?>(null)
    val clipboardContent = _clipboardContent.asStateFlow()

    fun tryUpdateClipboard() {
        val clipboardUri = clipboardManager.getFirstText()?.let { tryParseUriString(it) }
        if (clipboardUri != null && _clipboardContent.value != clipboardUri) {
            _clipboardContent.value = clipboardUri
        }
    }

    fun tryUpdateClipboard(label: String, uriStr: String) {
        val uri = tryParseUriString(uriStr)
        if (uri != null) {
            clipboardManager.setText(label, uri.toString())
        }
    }

    private val _showMiuiAlert = RefreshableStateFlow(false) { miuiCompat.showAlert(context) }
    val showMiuiAlert = _showMiuiAlert

    suspend fun updateMiuiAutoStartAppOp(activity: Activity): Boolean {
        val result = miuiCompat.startPermissionRequest(activity)
        _showMiuiAlert.refresh()

        return result
    }

    private val _defaultBrowser = { checkDefaultBrowser() }.asFlow()
    val defaultBrowser = _defaultBrowser

    private fun checkDefaultBrowser() = context.packageManager
        .resolveActivityCompat(BrowserResolver.httpBrowserIntent, PackageManager.MATCH_DEFAULT_ONLY)
        ?.activityInfo?.packageName == BuildConfig.APPLICATION_ID

    fun launchIntent(activity: Activity, intent: SettingsIntent): Boolean {
        return activity.startActivityWithConfirmation(Intent(intent.action))
    }

    private fun tryParseUriString(uriStr: String): Uri? {
        return UriUtil.parseWebUriStrict(uriStr)
    }

    fun enqueueNavEvent(destination: NavDestination, args: Bundle?) {
        analyticsService.enqueue(AnalyticsEvent.Navigate(destination.route ?: "<no_route>"))
    }

    fun updateTelemetryLevel(level: TelemetryLevel) {
        telemetryLevel(level)
        telemetryShowInfoDialog(false)
        analyticsService.changeLevel(level)
    }

    fun formatUseTime(): Pair<Int?, Int?>? {
        if (!LinkSheetAppConfig.showDonationBanner()) return null

        val duration = Duration.ofMillis(useTimeMs)
        val minutes = duration.toMinutesPart()
        if (minutes < BuildConfig.DONATION_BANNER_MIN) return null

        val hours = duration.toHoursPart()
        if (hours > 0) {
            return hours to null
        }

        return null to minutes
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getRequestRoleBrowserIntent() = roleManager!!.createRequestRoleIntent(
        RoleManager.ROLE_BROWSER
    )

    enum class SettingsIntent(val action: String) {
        DefaultApps(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
        DomainUrls("android.settings.MANAGE_DOMAIN_URLS"),
        CrossProfileAccess("android.settings.MANAGE_CROSS_PROFILE_ACCESS")
    }

    enum class BrowserStatus(
        @StringRes val headline: Int,
        @StringRes val subtitle: Int,
        val containerColor: @Composable () -> Color,
        val color: @Composable () -> Color,
        val icon: ImageVector,
        @StringRes val iconDescription: Int,
    ) {

        Known(
            R.string.at_least_one_known_browser_installed,
            R.string.at_least_one_known_browser_installed_explainer,
            { MaterialTheme.colorScheme.primaryContainer },
            { MaterialTheme.colorScheme.onSurface },
            Icons.Default.Public,
            R.string.success
        ),
        Unknown(
            R.string.at_least_one_unknown_browser_installer,
            R.string.at_least_one_unknown_browser_installer_explainer,
            { MaterialTheme.colorScheme.tertiaryContainer },
            { MaterialTheme.colorScheme.onTertiaryContainer },
            Icons.Default.Warning,
            R.string.warning
        ),
        None(
            R.string.no_browser_installed,
            R.string.no_browser_installed_explainer,
            { MaterialTheme.colorScheme.error },
            { MaterialTheme.colorScheme.onError },
            Icons.Default.Error,
            R.string.error
        );

        companion object {
            fun hasBrowser(browserStatus: BrowserStatus): Boolean {
                return browserStatus == Unknown || browserStatus == Known
            }
        }
    }

    fun hasBrowser(): BrowserStatus {
        val browsers = browserResolver.queryBrowsers()
        if (browsers.isEmpty()) return BrowserStatus.None
        if (browsers.any { KnownBrowser.isKnownBrowser(it.key) != null }) return BrowserStatus.Known
        return BrowserStatus.Unknown
    }
}
