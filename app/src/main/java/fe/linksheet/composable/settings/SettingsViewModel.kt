package fe.linksheet.composable.settings

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.preferred.PreferredResolver.resolve
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

    val whichAppsCanHandleLinks = mutableStateListOf<DisplayActivityInfo>()
    var enableCopyButton by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.enableCopyButton) ?: false
    )
    var singleTap by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.singleTap) ?: false
    )

    fun loadPreferredApps(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            preferredApps.clear()

            database.preferredAppDao().allPreferredApps().forEach {
                val displayActivityInfo = it.resolve(context)
                if (displayActivityInfo != null) {
                    preferredApps.getOrPut(displayActivityInfo) { mutableSetOf() }.add(it.host)
                }
            }
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

    @RequiresApi(Build.VERSION_CODES.S)
    fun loadAppsWhichCanHandleLinks(context: Context, filter: String? = null) {
        val manager = context.getSystemService(DomainVerificationManager::class.java)
        viewModelScope.launch(Dispatchers.IO) {
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
                    .filter {
                        if (filter != null) it.displayLabel.contains(
                            filter,
                            ignoreCase = true
                        ) else true
                    }
                    .sortedBy { it.displayLabel }
                    .toList()
            )
        }
    }

    fun onEnableCopyButton(it: Boolean) {
        this.enableCopyButton = it
        this.preferenceRepository.writeBoolean(PreferenceRepository.enableCopyButton, it)
    }

    fun onSingleTap(it: Boolean) {
        this.singleTap = it
        this.preferenceRepository.writeBoolean(PreferenceRepository.singleTap, it)
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