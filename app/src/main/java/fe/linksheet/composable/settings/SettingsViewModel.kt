package fe.linksheet.composable.settings

import android.app.AppOpsManager
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.verify.domain.DomainVerificationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.preferred.PreferredResolver.resolve
import com.tasomaniac.openwith.resolver.BrowserHandler
import com.tasomaniac.openwith.resolver.BrowserResolver
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.BuildConfig
import fe.linksheet.data.dao.PackageEntityDao
import fe.linksheet.data.entity.LibRedirectDefault
import fe.linksheet.data.entity.LibRedirectServiceState
import fe.linksheet.extension.allBrowsersIntent
import fe.linksheet.extension.filterIf
import fe.linksheet.extension.filterNullable
import fe.linksheet.extension.hasVerifiedDomains
import fe.linksheet.extension.launchIO
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
import fe.linksheet.resolver.InAppBrowserHandler
import fe.linksheet.ui.theme.Theme
import fe.linksheet.util.contextIO
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

    val preferredApps = mutableStateMapOf<DisplayActivityInfo, MutableSet<String>>()
    val preferredAppsFiltered = mutableStateMapOf<DisplayActivityInfo, MutableSet<String>>()

    val appsExceptPreferred = mutableStateListOf<DisplayActivityInfo>()

    val browsers = mutableStateListOf<DisplayActivityInfo>()
    val packages = mutableStateListOf<DisplayActivityInfo>()

    var browserMode = preferenceRepository.getState(Preferences.browserMode)
    var selectedBrowser = preferenceRepository.getStringState(Preferences.selectedBrowser)
    var inAppBrowserMode = preferenceRepository.getState(Preferences.inAppBrowserMode)

    private val whichAppsCanHandleLinks = mutableStateListOf<DisplayActivityInfo>()
    val whichAppsCanHandleLinksFiltered = mutableStateListOf<DisplayActivityInfo>()
    var whichAppsCanHandleLinksEnabled by mutableStateOf(true)
    var whichAppsCanHandleLinksLoading by mutableStateOf(false)

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


    suspend fun filterPreferredAppsAsync(filter: String) = contextIO {
        preferredAppsFiltered.clear()
        preferredApps.forEach { (info, hosts) ->
            if (filter.isEmpty() || info.displayLabel.contains(filter, ignoreCase = true)) {
                preferredAppsFiltered[info] = hosts
            }
        }
    }


    suspend fun loadPreferredApps(context: Context) = contextIO {
        preferredApps.clear()

//            val browsers = BrowserResolver.resolve(context).map { it.packageName }

        database.preferredAppDao().allPreferredApps().forEach { app ->
            val displayActivityInfo = app.resolve(context)

            if (displayActivityInfo != null
//                    && !browsers.contains(it.componentName.packageName)
            ) {
                preferredApps.getOrPut(displayActivityInfo) { mutableSetOf() }.add(app.host)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun loadAppsExceptPreferred(
        context: Context, manager: DomainVerificationManager
    ) = contextIO {
        val preferredAppsPackage = preferredApps.keys.mapToSet { it.packageName }

        appsExceptPreferred.setup(mapInstalledPackages(context, manager) {
            it.activityInfo.packageName !in preferredAppsPackage
        })
    }

    suspend fun loadBrowsers() = contextIO {
        browsers.setup(BrowserResolver.queryDisplayActivityInfoBrowsers(true))
    }

    suspend fun loadPackages(context: Context) = contextIO {
        packages.setup(
            context.packageManager
                .queryAllResolveInfos(true)
                .toDisplayActivityInfo(context, true)
        )

        Log.d("LoadPackages", "${packages.toList()}")
    }


    suspend fun insertPreferredAppAsync(preferredApp: PreferredApp) = contextIO {
        database.preferredAppDao().insert(preferredApp)
    }

    suspend fun insertPreferredAppsAsync(preferredApps: List<PreferredApp>) = contextIO {
        database.preferredAppDao().insert(preferredApps)
    }

    suspend fun deletePreferredAppAsync(host: String, packageName: String) {
        Timber.tag("DeletePreferredApp").d(host)
        contextIO {
            database.preferredAppDao().deleteByPackageNameAndHost(host, packageName)
        }
    }

    suspend fun deletePreferredAppWherePackageAsync(packageName: String) = contextIO {
        database.preferredAppDao().deleteByPackageName(packageName)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getRequestRoleBrowserIntent(roleManager: RoleManager): Intent {
        return roleManager.createRequestRoleIntent(RoleManager.ROLE_BROWSER)
    }

    fun openDefaultBrowserSettings(context: Context): Boolean {
        return context.startActivityWithConfirmation(intentManageDefaultAppSettings)
    }

    @Throws(ActivityNotFoundException::class)
    fun openOpenByDefaultSettings(context: Context, packageName: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
                // S*msung moment lol 🤮 (https://stackoverflow.com/a/72365164)
                Intent("android.settings.MANAGE_DOMAIN_URLS")
            } else {
                Intent(
                    Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                    Uri.parse("package:$packageName")
                )
            }

            context.startActivity(intent)
            return true
        }

        return false
    }


    fun checkDefaultBrowser(context: Context) = context.packageManager
        .resolveActivityCompat(allBrowsersIntent, PackageManager.MATCH_DEFAULT_ONLY)
        ?.activityInfo?.packageName == BuildConfig.APPLICATION_ID


    suspend fun filterWhichAppsCanHandleLinksAsync(filter: String) = contextIO {
        whichAppsCanHandleLinksFiltered.clear()
        whichAppsCanHandleLinksFiltered.addAll(whichAppsCanHandleLinks.filterIf(filter.isNotEmpty()) {
            it.displayLabel.contains(filter, ignoreCase = true)
        })
    }

    fun onWhichAppsCanHandleLinksEnabled(it: Boolean) {
        this.whichAppsCanHandleLinksEnabled = it
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun mapInstalledPackages(
        context: Context,
        manager: DomainVerificationManager,
        filter: ((ResolveInfo) -> Boolean)? = null
    ) = context.packageManager.queryAllResolveInfos(true)
        .filterNullable(filter)
        .filter { manager.hasVerifiedDomains(it, whichAppsCanHandleLinksEnabled) }
        .toDisplayActivityInfo(context, true)


    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun loadAppsWhichCanHandleLinksAsync(
        context: Context,
        manager: DomainVerificationManager
    ) = contextIO({ whichAppsCanHandleLinksLoading = it }) {
        whichAppsCanHandleLinks.setup(mapInstalledPackages(context, manager) { true })
    }

    fun updateBrowserMode(mode: BrowserHandler.BrowserMode) {
        if (this.browserMode.value == BrowserHandler.BrowserMode.SelectedBrowser && this.browserMode.value != mode && this.selectedBrowser.value != null) {
            launchIO { deletePreferredAppWherePackageAsync(selectedBrowser.value!!) }
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

    suspend fun queryWhitelistedBrowsersAsync() = contextIO {
        whitelistedBrowserMap.clear()
        val whitelistedBrowsers = database.whitelistedBrowsersDao().getAll().mapToSet { it.packageName }

        browsers.forEach { info ->
            whitelistedBrowserMap[info] = info.packageName in whitelistedBrowsers
        }
    }

    suspend fun saveWhitelistedBrowsers() = contextIO {
        val dao = database.whitelistedBrowsersDao()
        whitelistedBrowserMap.forEach { (app, enabled) ->
            dao.insertOrDelete(PackageEntityDao.Mode.fromBool(enabled), app.packageName)
        }
    }

    suspend fun queryAppsForInAppBrowserDisableInSelected() = contextIO {
        disableInAppBrowserInSelectedMap.clear()
        val disableInAppBrowserInSelected = database.disableInAppBrowserInSelectedDao()
            .getAll()
            .mapToSet { it.packageName }

        packages.forEach { info ->
            disableInAppBrowserInSelectedMap[info] = info.packageName in disableInAppBrowserInSelected
        }
    }


    suspend fun saveInAppBrowserDisableInSelected() = contextIO {
        val dao = database.disableInAppBrowserInSelectedDao()
        disableInAppBrowserInSelectedMap.forEach { (app, enabled) ->
            dao.insertOrDelete(PackageEntityDao.Mode.fromBool(enabled), app.packageName)
        }
    }

    suspend fun saveLibRedirectDefault(
        serviceKey: String,
        frontendKey: String,
        instanceUrl: String
    ) = contextIO {
        database.libRedirectDefaultDao()
            .insert(LibRedirectDefault(serviceKey, frontendKey, instanceUrl))
    }

    suspend fun loadLibRedirectDefault(serviceKey: String) = contextIO {
        libRedirectDefault =
            database.libRedirectDefaultDao().getLibRedirectDefaultByServiceKey(serviceKey)
    }

    suspend fun loadLibRedirectState(serviceKey: String) = contextIO {
        libRedirectEnabled =
            database.libRedirectServiceStateDao().getLibRedirectServiceState(serviceKey)?.enabled
    }

    suspend fun updateLibRedirectState(serviceKey: String, boolean: Boolean) = contextIO {
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