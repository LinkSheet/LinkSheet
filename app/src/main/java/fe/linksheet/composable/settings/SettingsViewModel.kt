package fe.linksheet.composable.settings

import android.app.AppOpsManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.preferred.PreferredResolver.resolve
import com.tasomaniac.openwith.resolver.BrowserHandler
import com.tasomaniac.openwith.resolver.BrowserResolver
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.BuildConfig
import fe.linksheet.extension.queryFirstIntentActivityByPackageNameOrNull
import fe.linksheet.extension.toDisplayActivityInfo
import fe.linksheet.module.preference.PreferenceRepository
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class SettingsViewModel : ViewModel(), KoinComponent {
    companion object {
        val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("http://example.com"))
        val intentManageDefaultAppSettings = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
    }

    private val database by inject<LinkSheetDatabase>()
    private val preferenceRepository by inject<PreferenceRepository>()

    val preferredApps = mutableStateMapOf<DisplayActivityInfo, MutableSet<String>>()
    val preferredAppsFiltered = mutableStateMapOf<DisplayActivityInfo, MutableSet<String>>()

    val browsers = mutableStateListOf<DisplayActivityInfo>()

    var browserMode by mutableStateOf(
        preferenceRepository.getString(
            PreferenceRepository.browserMode,
            BrowserHandler.BrowserMode.persister,
            BrowserHandler.BrowserMode.reader
        )
    )

    var selectedBrowser by mutableStateOf(preferenceRepository.getString(PreferenceRepository.selectedBrowser))

    private val whichAppsCanHandleLinks = mutableStateListOf<DisplayActivityInfo>()
    val whichAppsCanHandleLinksFiltered = mutableStateListOf<DisplayActivityInfo>()
    var whichAppsCanHandleLinksEnabled by mutableStateOf(true)
    var whichAppsCanHandleLinksLoading by mutableStateOf(false)

    var usageStatsSorting by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.usageStatsSorting) ?: false
    )
    var wasTogglingUsageStatsSorting by mutableStateOf(false)

    var enableCopyButton by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.enableCopyButton) ?: false
    )
    var hideAfterCopying by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.hideAfterCopying) ?: false
    )

    var singleTap by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.singleTap) ?: false
    )

    var enableSendButton by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.enableSendButton) ?: false
    )

    var alwaysShowPackageName by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.alwaysShowPackageName) ?: false
    )

    var disableToasts by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.disableToasts) ?: false
    )

    var gridLayout by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.gridLayout) ?: false
    )


    suspend fun filterPreferredAppsAsync(filter: String) {
        return withContext(Dispatchers.IO) {
            preferredAppsFiltered.clear()
            preferredApps.forEach { (info, hosts) ->
                if (filter.isEmpty() || info.displayLabel.contains(filter, ignoreCase = true)) {
                    preferredAppsFiltered[info] = hosts
                }
            }
        }
    }

    suspend fun loadPreferredApps(context: Context) {
        withContext(Dispatchers.IO) {
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
    }

    fun loadBrowsers(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            browsers.clear()
            browsers.addAll(BrowserResolver.resolve(context).sortedBy { it.displayLabel })
        }
    }

    suspend fun insertPreferredAppAsync(preferredApp: PreferredApp) {
        return withContext(Dispatchers.IO) {
            database.preferredAppDao().insert(preferredApp)
        }
    }

    suspend fun insertPreferredAppsAsync(preferredApps: List<PreferredApp>) {
        return withContext(Dispatchers.IO) {
            database.preferredAppDao().insert(preferredApps)
        }
    }

    suspend fun deletePreferredAppAsync(host: String, packageName: String) {
        Log.d("DeletePreferredApp", host)
        return withContext(Dispatchers.IO) {
            database.preferredAppDao().deleteByPackageNameAndHost(host, packageName)
        }
    }

    suspend fun deletePreferredAppWherePackageAsync(packageName: String) {
        return withContext(Dispatchers.IO) {
            database.preferredAppDao().deleteByPackageName(packageName)
        }
    }

    fun openDefaultBrowserSettings(context: Context) {
        context.startActivity(intentManageDefaultAppSettings)
    }

    @Throws(ActivityNotFoundException::class)
    fun openOpenByDefaultSettings(context: Context, packageName: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(
                Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                Uri.parse("package:$packageName")
            )
            context.startActivity(intent)
            return true
        }

        return false
    }

    fun checkDefaultBrowser(context: Context) = context.packageManager
        .resolveActivity(intentBrowser, PackageManager.MATCH_DEFAULT_ONLY)
        ?.activityInfo?.packageName == BuildConfig.APPLICATION_ID


    suspend fun filterWhichAppsCanHandleLinksAsync(filter: String): Boolean {
        return withContext(Dispatchers.IO) {
            whichAppsCanHandleLinksFiltered.clear()
            whichAppsCanHandleLinksFiltered.addAll(whichAppsCanHandleLinks.filter {
                if (filter.isNotEmpty()) it.displayLabel.contains(
                    filter,
                    ignoreCase = true
                ) else true
            })
        }
    }

    fun onWhichAppsCanHandleLinksEnabled(it: Boolean){
        this.whichAppsCanHandleLinksEnabled = it
    }

    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun loadAppsWhichCanHandleLinksAsync(
        context: Context,
        manager: DomainVerificationManager
    ) {
        whichAppsCanHandleLinksLoading = true
        return withContext(Dispatchers.IO) {
            whichAppsCanHandleLinks.clear()
            whichAppsCanHandleLinks.addAll(
                context.packageManager.getInstalledPackages(PackageManager.MATCH_ALL)
                    .asSequence()
                    .mapNotNull { packageInfo ->
                        context.packageManager.queryFirstIntentActivityByPackageNameOrNull(
                            packageInfo.packageName
                        )
                    }
                    .filter { resolveInfo ->
                        val state = manager
                            .getDomainVerificationUserState(resolveInfo.activityInfo.packageName)
                        state != null
                                && (if (whichAppsCanHandleLinksEnabled) state.isLinkHandlingAllowed else !state.isLinkHandlingAllowed)
                                && state.hostToStateMap.isNotEmpty()
                                && state.hostToStateMap.any { it.value == DomainVerificationUserState.DOMAIN_STATE_VERIFIED }
                    }
                    .filter { it.activityInfo.packageName != BuildConfig.APPLICATION_ID }
                    .map { it.toDisplayActivityInfo(context) }
                    .sortedBy { it.displayLabel }
                    .toList()
            )

            whichAppsCanHandleLinksLoading = false
        }
    }

    fun onBrowserMode(it: BrowserHandler.BrowserMode) {
        if (this.browserMode == BrowserHandler.BrowserMode.SelectedBrowser && this.browserMode != it && this.selectedBrowser != null) {
            viewModelScope.launch(Dispatchers.IO) {
                deletePreferredAppWherePackageAsync(selectedBrowser!!)
            }
        }

        this.browserMode = it
        this.preferenceRepository.writeString(
            PreferenceRepository.browserMode,
            it,
            BrowserHandler.BrowserMode.persister
        )
    }

    fun onSelectedBrowser(it: String) {
        this.selectedBrowser = it
        this.preferenceRepository.writeString(PreferenceRepository.selectedBrowser, it)
    }

    fun onUsageStatsSorting(it: Boolean) {
        this.usageStatsSorting = it
        this.preferenceRepository.writeBoolean(PreferenceRepository.usageStatsSorting, it)
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

    fun onEnableCopyButton(it: Boolean) {
        this.enableCopyButton = it
        this.preferenceRepository.writeBoolean(PreferenceRepository.enableCopyButton, it)
    }

    fun onHideAfterCopying(it: Boolean) {
        this.hideAfterCopying = it
        this.preferenceRepository.writeBoolean(PreferenceRepository.hideAfterCopying, it)
    }

    fun onSingleTap(it: Boolean) {
        this.singleTap = it
        this.preferenceRepository.writeBoolean(PreferenceRepository.singleTap, it)
    }

    fun onSendButton(it: Boolean) {
        this.enableSendButton = it
        this.preferenceRepository.writeBoolean(PreferenceRepository.enableSendButton, it)
    }

    fun onAlwaysShowButton(it: Boolean) {
        this.alwaysShowPackageName = it
        this.preferenceRepository.writeBoolean(PreferenceRepository.alwaysShowPackageName, it)
    }

    fun onDisableToasts(it: Boolean) {
        this.disableToasts = it
        this.preferenceRepository.writeBoolean(PreferenceRepository.disableToasts, it)
    }

    fun onGridLayout(it: Boolean) {
        this.gridLayout = it
        this.preferenceRepository.writeBoolean(PreferenceRepository.gridLayout, it)
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