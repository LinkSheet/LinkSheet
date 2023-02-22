package fe.linksheet.composable.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.verify.domain.DomainVerificationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.preferred.PreferredResolver.resolve
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.BuildConfig
import fe.linksheet.extension.toDisplayActivityInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.lang.reflect.Field


class SettingsViewModel : ViewModel(), KoinComponent {
    private val database by inject<LinkSheetDatabase>()

    val preferredApps = mutableStateListOf<Pair<String, DisplayActivityInfo?>>()

    fun loadPreferredApps(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            preferredApps.clear()
            preferredApps.addAll(database.preferredAppDao().allPreferredApps().map {
                it.host to it.resolve(context)
            })
        }
    }

    fun deletePreferredApp(host: String) {
        viewModelScope.launch(Dispatchers.IO) {
            preferredApps.removeIf { it.first == host }
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
    fun loadAppsWhichCanHandleLinks(context: Context): List<DisplayActivityInfo> {
        val manager = context.getSystemService(DomainVerificationManager::class.java)

        return context.packageManager.getInstalledApplications(PackageManager.MATCH_ALL)
            .asSequence()
            .mapNotNull {
                context.packageManager.resolveActivity(
                    Intent().setPackage(it.packageName),
                    PackageManager.MATCH_ALL
                )
            }
            .filter {
                val state = manager.getDomainVerificationUserState(it.activityInfo.packageName)
                state != null && state.isLinkHandlingAllowed && state.hostToStateMap.isNotEmpty()
            }
            .filter { it.activityInfo.packageName != BuildConfig.APPLICATION_ID }
            .map { it.toDisplayActivityInfo(context) }
            .toList()
    }

    companion object {
        const val PRIVATE_FLAG_HAS_DOMAIN_URLS = (1 shl 4)
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun doesAppHandleLinks(context: Context, packageName: String): Boolean? {
        val packageInfo = context.packageManager.getPackageInfo(packageName, 0)

        val field = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val fields =
                HiddenApiBypass.getInstanceFields(ApplicationInfo::class.java) as List<Field>
            fields.find { it.name == "privateFlags" }?.get(packageInfo.applicationInfo)
        } else {
            ApplicationInfo::class.java.getDeclaredField("privateFlags").apply {
                this.isAccessible = true
            }.get(packageInfo.applicationInfo)
        }

        return field?.let {
            // https://android.googlesource.com/platform/frameworks/base/+/android-8.0.0_r4/cmds/pm/src/com/android/commands/pm/Pm.java#898
            it.toString().toInt() and PRIVATE_FLAG_HAS_DOMAIN_URLS != 0
        }
    }
}