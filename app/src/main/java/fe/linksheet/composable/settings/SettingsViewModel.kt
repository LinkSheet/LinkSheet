package fe.linksheet.composable.settings

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import fe.linksheet.module.database.LinkSheetDatabase
import fe.linksheet.BuildConfig
import fe.linksheet.extension.allBrowsersIntent
import fe.linksheet.extension.resolveActivityCompat
import fe.linksheet.extension.startActivityWithConfirmation
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.*


class SettingsViewModel(
    val database: LinkSheetDatabase,
    val preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    companion object {
        val intentManageDefaultAppSettings = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
    }

    var theme = preferenceRepository.getState(Preferences.theme)

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getRequestRoleBrowserIntent(roleManager: RoleManager): Intent {
        return roleManager.createRequestRoleIntent(RoleManager.ROLE_BROWSER)
    }

    fun openDefaultBrowserSettings(activity: Activity): Boolean {
        return activity.startActivityWithConfirmation(intentManageDefaultAppSettings)
    }

    fun checkDefaultBrowser(context: Context) = context.packageManager
        .resolveActivityCompat(allBrowsersIntent, PackageManager.MATCH_DEFAULT_ONLY)
        ?.activityInfo?.packageName == BuildConfig.APPLICATION_ID
}