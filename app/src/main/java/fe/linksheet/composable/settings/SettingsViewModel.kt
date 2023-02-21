package fe.linksheet.composable.settings

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.preferred.PreferredResolver.resolve
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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

    fun openDefaultBrowserSettings(context: Context): Boolean {
        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        if (context.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            ).isNotEmpty()
        ) {
            context.startActivity(intent)
            return true
        }

        return false
    }

    fun checkDefaultBrowser(context: Context): Boolean {
        val browserIntent = Intent("android.intent.action.VIEW", Uri.parse("http://"))
        val resolveInfo: ResolveInfo? =
            context.packageManager.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)

        return resolveInfo?.activityInfo?.packageName == BuildConfig.APPLICATION_ID
    }
}