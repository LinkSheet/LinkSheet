package fe.linksheet.module.viewmodel

import android.app.Activity
import android.app.Application
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import fe.linksheet.BuildConfig
import fe.linksheet.extension.android.allBrowsersIntent
import fe.linksheet.extension.android.resolveActivityCompat
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.android.preference.helper.PreferenceRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel

class MainViewModel(
    val context: Application,
    val preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {

    @RequiresApi(Build.VERSION_CODES.Q)
    private val roleManager = context.getSystemService<RoleManager>()!!

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getRequestRoleBrowserIntent() = roleManager.createRequestRoleIntent(
        RoleManager.ROLE_BROWSER
    )

    fun openDefaultBrowserSettings(
        activity: Activity
    ) = activity.startActivityWithConfirmation(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))

    fun checkDefaultBrowser() = context.packageManager
        .resolveActivityCompat(allBrowsersIntent, PackageManager.MATCH_DEFAULT_ONLY)
        ?.activityInfo?.packageName == BuildConfig.APPLICATION_ID
}