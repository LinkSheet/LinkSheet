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
import fe.linksheet.extension.mapToSet
import fe.linksheet.extension.queryAllResolveInfos
import fe.linksheet.extension.resolveActivityCompat
import fe.linksheet.extension.setup
import fe.linksheet.extension.startActivityWithConfirmation
import fe.linksheet.extension.toDisplayActivityInfo
import fe.linksheet.module.preference.BasePreference
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.preference.RepositoryState
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber


class SettingsViewModel : ViewModel(), KoinComponent {
    companion object {
        val intentManageDefaultAppSettings = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        const val libRedirectRandomInstanceKey = "RANDOM_INSTANCE"
    }

    private val database by inject<LinkSheetDatabase>()
    private val preferenceRepository by inject<PreferenceRepository>()



    val browsers = mutableStateListOf<DisplayActivityInfo>()
    val packages = mutableStateListOf<DisplayActivityInfo>()

    var browserMode = preferenceRepository.getState(Preferences.browserMode)
    var selectedBrowser = preferenceRepository.getStringState(Preferences.selectedBrowser)
    var inAppBrowserMode = preferenceRepository.getState(Preferences.inAppBrowserMode)

    val whitelistedBrowserMap = mutableStateMapOf<DisplayActivityInfo, Boolean>()
    val disableInAppBrowserInSelectedMap = mutableStateMapOf<DisplayActivityInfo, Boolean>()

    var libRedirectDefault by mutableStateOf<LibRedirectDefault?>(null)
    var libRedirectEnabled by mutableStateOf<Boolean?>(null)

    var usageStatsSorting = preferenceRepository.getBooleanState(Preferences.usageStatsSorting)

    var wasTogglingUsageStatsSorting by mutableStateOf(false)

    var enableCopyButton = preferenceRepository.getBooleanState(Preferences.enableCopyButton)
    var hideAfterCopying = preferenceRepository.getBooleanState(Preferences.hideAfterCopying)
    var singleTap = preferenceRepository.getBooleanState(Preferences.singleTap)
    var enableSendButton = preferenceRepository.getBooleanState(Preferences.enableSendButton)
    var alwaysShowPackageName =
        preferenceRepository.getBooleanState(Preferences.alwaysShowPackageName)
    var disableToasts = preferenceRepository.getBooleanState(Preferences.disableToasts)
    var gridLayout = preferenceRepository.getBooleanState(Preferences.gridLayout)
    var useClearUrls = preferenceRepository.getBooleanState(Preferences.useClearUrls)
    var useFastForwardRules = preferenceRepository.getBooleanState(Preferences.useFastForwardRules)
    var enableLibRedirect = preferenceRepository.getBooleanState(Preferences.enableLibRedirect)
    var followRedirects = preferenceRepository.getBooleanState(Preferences.followRedirects)
    var followRedirectsLocalCache =
        preferenceRepository.getBooleanState(Preferences.followRedirectsLocalCache)
    var followRedirectsExternalService =
        preferenceRepository.getBooleanState(Preferences.followRedirectsExternalService)
    var followOnlyKnownTrackers =
        preferenceRepository.getBooleanState(Preferences.followOnlyKnownTrackers)
    var enableDownloader = preferenceRepository.getBooleanState(Preferences.enableDownloader)
    var downloaderCheckUrlMimeType =
        preferenceRepository.getBooleanState(Preferences.downloaderCheckUrlMimeType)


    var theme = preferenceRepository.getState(Preferences.theme)
    var dontShowFilteredItem = preferenceRepository.getBooleanState(Preferences.dontShowFilteredItem)
    var useTextShareCopyButtons = preferenceRepository.getBooleanState(Preferences.useTextShareCopyButtons)
    var previewUrl = preferenceRepository.getBooleanState(Preferences.previewUrl)




    suspend fun loadBrowsers() = ioAsync {
        browsers.setup(BrowserResolver.queryDisplayActivityInfoBrowsers(true))
    }

    suspend fun loadPackages(context: Context) = ioAsync {
        packages.setup(
            context.packageManager
                .queryAllResolveInfos(true)
                .toDisplayActivityInfo(context, true)
        )

        Log.d("LoadPackages", "${packages.toList()}")
    }


    suspend fun insertPreferredAppAsync(preferredApp: PreferredApp) = ioAsync {
        database.preferredAppDao().insert(preferredApp)
    }

    suspend fun insertPreferredAppsAsync(preferredApps: List<PreferredApp>) = ioAsync {
        database.preferredAppDao().insert(preferredApps)
    }

    suspend fun deletePreferredAppAsync(host: String, packageName: String) {
        Timber.tag("DeletePreferredApp").d(host)
        ioAsync {
            database.preferredAppDao().deleteByHostAndPackageName(host, packageName)
        }
    }

    suspend fun deletePreferredAppWherePackageAsync(packageName: String) = ioAsync {
        database.preferredAppDao().deleteByPackageName(packageName)
    }

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


    fun updateBrowserMode(mode: BrowserHandler.BrowserMode) {
        if (this.browserMode.value == BrowserHandler.BrowserMode.SelectedBrowser && this.browserMode.value != mode && this.selectedBrowser.value != null) {
            ioAsync { deletePreferredAppWherePackageAsync(selectedBrowser.value!!) }
        }

        this.browserMode.updateState(mode)
    }

    fun <T, NT, P : BasePreference<T, NT>> updateState(state: RepositoryState<T, NT, P>, newState: NT) {
        state.updateState(newState)
    }

    fun openUsageStatsSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        wasTogglingUsageStatsSorting = true
    }

    fun getUsageStatsAllowed(context: Context): Boolean {
        val appOps = context.getSystemService<AppOpsManager>()
        val mode = appOps!!.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )

        return mode == AppOpsManager.MODE_ALLOWED
    }

    suspend fun queryWhitelistedBrowsersAsync() = ioAsync {
        whitelistedBrowserMap.clear()
        val whitelistedBrowsers = database.whitelistedBrowsersDao().getAll().mapToSet { it.packageName }

        browsers.forEach { info ->
            whitelistedBrowserMap[info] = info.packageName in whitelistedBrowsers
        }
    }

    suspend fun saveWhitelistedBrowsers() = ioAsync {
        val dao = database.whitelistedBrowsersDao()
        whitelistedBrowserMap.forEach { (app, enabled) ->
            dao.insertOrDelete(PackageEntityDao.Mode.fromBool(enabled), app.packageName)
        }
    }

    suspend fun queryAppsForInAppBrowserDisableInSelected() = ioAsync {
        disableInAppBrowserInSelectedMap.clear()
        val disableInAppBrowserInSelected = database.disableInAppBrowserInSelectedDao()
            .getAll()
            .mapToSet { it.packageName }

        packages.forEach { info ->
            disableInAppBrowserInSelectedMap[info] = info.packageName in disableInAppBrowserInSelected
        }
    }


    suspend fun saveInAppBrowserDisableInSelected() = ioAsync {
        val dao = database.disableInAppBrowserInSelectedDao()
        disableInAppBrowserInSelectedMap.forEach { (app, enabled) ->
            dao.insertOrDelete(PackageEntityDao.Mode.fromBool(enabled), app.packageName)
        }
    }

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