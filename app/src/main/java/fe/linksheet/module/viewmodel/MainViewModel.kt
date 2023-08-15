package fe.linksheet.module.viewmodel

import android.app.Activity
import android.app.Application
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.getSystemService
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.BuildConfig
import fe.linksheet.R
import fe.linksheet.extension.android.resolveActivityCompat
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.resolver.BrowserHandler
import fe.linksheet.module.resolver.BrowserResolver
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.AndroidVersion


class MainViewModel(
    val context: Application,
    val preferenceRepository: PreferenceRepository,
    val browserResolver: BrowserResolver,
    featureFlagRepository: PreferenceRepository,
) : BaseViewModel(preferenceRepository) {

    val featureFlagShizuku = featureFlagRepository.getBooleanState(Preferences.featureFlagShizuku)
    val firstRun = preferenceRepository.getBooleanState(Preferences.firstRun)

    private val roleManager by lazy {
        if (AndroidVersion.AT_LEAST_API_26_O) {
            context.getSystemService<RoleManager>()
        } else null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getRequestRoleBrowserIntent() = roleManager!!.createRequestRoleIntent(
        RoleManager.ROLE_BROWSER
    )

    fun openDefaultBrowserSettings(
        activity: Activity
    ) = activity.startActivityWithConfirmation(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))

    fun checkDefaultBrowser() = context.packageManager
        .resolveActivityCompat(BrowserResolver.httpBrowserIntent, PackageManager.MATCH_DEFAULT_ONLY)
        ?.activityInfo?.packageName == BuildConfig.APPLICATION_ID

    enum class BrowserStatus(
        @StringRes val headline: Int,
        @StringRes val subtitle: Int,
        val containerColor: @Composable () -> Color,
        val color: @Composable () -> Color,
        val icon: ImageVector,
        @StringRes val iconDescription: Int
    ) {

        Known(
            R.string.at_least_one_known_browser_installed,
            R.string.at_least_one_known_browser_installed_explainer,
            { MaterialTheme.colorScheme.primaryContainer },
            { MaterialTheme.colorScheme.onSurface },
            Icons.Default.CheckCircle,
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
    }

    fun hasBrowser(): BrowserStatus {
        val browsers = browserResolver.queryBrowsers()
        if (browsers.isEmpty()) return BrowserStatus.None
        if (browsers.any { BrowserResolver.KnownBrowsers.isKnownBrowser(it.key) != null }) return BrowserStatus.Known
        return BrowserStatus.Unknown
    }
}