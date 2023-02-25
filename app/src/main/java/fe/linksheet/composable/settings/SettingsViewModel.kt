package fe.linksheet.composable.settings

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class SettingsViewModel : ViewModel(), KoinComponent {
    private val database by inject<LinkSheetDatabase>()
    private val preferenceRepository by inject<PreferenceRepository>()

    val preferredApps = mutableStateMapOf<DisplayActivityInfo, MutableSet<String>>()

    val browsers = mutableStateListOf<DisplayActivityInfo>()

    var browserMode by mutableStateOf(
        preferenceRepository.getString(
            PreferenceRepository.browserMode,
            BrowserHandler.BrowserMode.persister,
            BrowserHandler.BrowserMode.reader
        )
    )

    var selectedBrowser by mutableStateOf(preferenceRepository.getString(PreferenceRepository.selectedBrowser))


    val whichAppsCanHandleLinks = mutableStateListOf<DisplayActivityInfo>()
    val whichAppsCanHandleLinksFiltered = mutableStateListOf<DisplayActivityInfo>()

    var usageStatsSorting by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.usageStatsSorting) ?: false
    )
    var wasTogglingUsageStatsSorting by mutableStateOf(false)

    var enableCopyButton by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.enableCopyButton) ?: false
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

    fun loadPreferredApps(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            preferredApps.clear()

//            val browsers = BrowserResolver.resolve(context).map { it.packageName }

            database.preferredAppDao().allPreferredApps().forEach { it ->
                val displayActivityInfo = it.resolve(context)

                if (displayActivityInfo != null
//                    && !browsers.contains(it.componentName.packageName)
                ) {
                    preferredApps.getOrPut(displayActivityInfo) { mutableSetOf() }.add(it.host)
                }
            }
        }
    }

    fun loadBrowsers(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            browsers.clear()
            browsers.addAll(BrowserResolver.resolve(context))
        }
    }

    fun insertPreferredAppAsync(preferredApp: PreferredApp): Deferred<Unit> {
        return viewModelScope.async(Dispatchers.IO) {
            database.preferredAppDao().insert(preferredApp)
        }
    }

    fun insertPreferredAppsAsync(preferredApps: List<PreferredApp>): Deferred<Unit> {
        return viewModelScope.async(Dispatchers.IO) {
            database.preferredAppDao().insert(preferredApps)
        }
    }

    fun deletePreferredAppAsync(host: String): Deferred<Unit> {
        return viewModelScope.async(Dispatchers.IO) {
            database.preferredAppDao().deleteHost(host)
        }
    }

    fun openDefaultBrowserSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
    }

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

    fun checkDefaultBrowser(context: Context): Boolean {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"))
        val resolveInfo =
            context.packageManager.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)

        return resolveInfo?.activityInfo?.packageName == BuildConfig.APPLICATION_ID
    }

    fun filterWhichAppsCanHandleLinksAsync(filter: String): Deferred<Boolean> {
        return viewModelScope.async(Dispatchers.IO) {
            whichAppsCanHandleLinksFiltered.clear()
            whichAppsCanHandleLinksFiltered.addAll(whichAppsCanHandleLinks.filter {
                if (filter.isNotEmpty()) it.displayLabel.contains(
                    filter,
                    ignoreCase = true
                ) else true
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun loadAppsWhichCanHandleLinksAsync(
        context: Context,
        manager: DomainVerificationManager
    ): Deferred<Boolean> {
        return viewModelScope.async(Dispatchers.IO) {
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
                        val state =
                            manager.getDomainVerificationUserState(resolveInfo.activityInfo.packageName)
                        state != null && state.isLinkHandlingAllowed && state.hostToStateMap.isNotEmpty() && state.hostToStateMap.any { it.value == DomainVerificationUserState.DOMAIN_STATE_VERIFIED }
                    }
                    .filter { it.activityInfo.packageName != BuildConfig.APPLICATION_ID }
                    .map { it.toDisplayActivityInfo(context) }
                    .sortedBy { it.displayLabel }
                    .toList()
            )
        }
    }

    fun onBrowserMode(it: BrowserHandler.BrowserMode) {
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