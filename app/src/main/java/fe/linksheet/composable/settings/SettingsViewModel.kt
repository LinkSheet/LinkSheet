package fe.linksheet.composable.settings

import android.app.Activity
import android.app.AppOpsManager
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.resolver.BrowserHandler
import com.tasomaniac.openwith.resolver.BrowserResolver
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.BuildConfig
import fe.linksheet.data.dao.base.PackageEntityDao
import fe.linksheet.data.entity.LibRedirectDefault
import fe.linksheet.data.entity.LibRedirectServiceState
import fe.linksheet.extension.allBrowsersIntent
import fe.linksheet.extension.ioAsync
import fe.linksheet.extension.ioLaunch
import fe.linksheet.extension.mapToSet
import fe.linksheet.extension.queryAllResolveInfos
import fe.linksheet.extension.resolveActivityCompat
import fe.linksheet.extension.setup
import fe.linksheet.extension.startActivityWithConfirmation
import fe.linksheet.extension.toDisplayActivityInfos
import fe.linksheet.module.preference.BasePreference
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.preference.RepositoryState
import fe.linksheet.module.viewmodel.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber


class SettingsViewModel(
    val database: LinkSheetDatabase,
    val preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    companion object {
        val intentManageDefaultAppSettings = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        const val libRedirectRandomInstanceKey = "RANDOM_INSTANCE"
    }

    var inAppBrowserMode = preferenceRepository.getState(Preferences.inAppBrowserMode)

    var libRedirectDefault by mutableStateOf<LibRedirectDefault?>(null)
    var libRedirectEnabled by mutableStateOf<Boolean?>(null)
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


    suspend fun saveLibRedirectDefault(
        serviceKey: String,
        frontendKey: String,
        instanceUrl: String
    ) = ioAsync {
        database.libRedirectDefaultDao()
            .insert(LibRedirectDefault(serviceKey, frontendKey, instanceUrl))
    }

    suspend fun loadLibRedirectDefault(serviceKey: String) = ioAsync {
        libRedirectDefault =
            database.libRedirectDefaultDao().getByServiceKey(serviceKey)
    }

    suspend fun loadLibRedirectState(serviceKey: String) = ioAsync {
        libRedirectEnabled =
            database.libRedirectServiceStateDao().getServiceState(serviceKey)?.enabled
    }

    suspend fun updateLibRedirectState(serviceKey: String, boolean: Boolean) = ioAsync {
        database.libRedirectServiceStateDao()
            .insert(LibRedirectServiceState(serviceKey, boolean))
    }

//    companion object {
//        const val PRIVATE_FLAG_HAS_DOMAIN_URLS = (1 shl 4)
//    }
//
//    @SuppressLint("DiscouragedPrivateApi")
//    private fun doesAppHandleLinks(context: Context, packageName: String): Boolean? {
//        val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
//
//        val field = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            val fields =
//                HiddenApiBypass.getInstanceFields(ApplicationInfo::class.java) as List<Field>
//            fields.find { it.name == "privateFlags" }?.get(packageInfo.applicationInfo)
//        } else {
//            ApplicationInfo::class.java.getDeclaredField("privateFlags").apply {
//                this.isAccessible = true
//            }.get(packageInfo.applicationInfo)
//        }
//
//        return field?.let {
//            // https://android.googlesource.com/platform/frameworks/base/+/android-8.0.0_r4/cmds/pm/src/com/android/commands/pm/Pm.java#898
//            it.toString().toInt() and PRIVATE_FLAG_HAS_DOMAIN_URLS != 0
//        }
//    }
}