package fe.linksheet.composable.settings.apps.link

import android.content.Context
import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.extension.filterIf
import fe.linksheet.extension.getDisplayActivityInfos
import fe.linksheet.extension.ioAsync
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class AppsWhichCanOpenLinksViewModel : ViewModel(), KoinComponent {
    private val context by inject<Context>()

    @RequiresApi(Build.VERSION_CODES.S)
    private val domainVerificationManager = context.getSystemService<DomainVerificationManager>()!!

    private val apps = mutableStateListOf<DisplayActivityInfo>()
    val appsFiltered = mutableStateListOf<DisplayActivityInfo>()
    var loading = mutableStateOf(false)
        private set

    var linkHandlingAllowed by mutableStateOf(true)

    @RequiresApi(Build.VERSION_CODES.S)
    fun loadAppsAsync() = ioAsync(loading, apps) {
        domainVerificationManager.getDisplayActivityInfos(context, linkHandlingAllowed)
    }

    fun filterAppsAsync(filter: String) = ioAsync(appsFiltered) {
        apps.filterIf(filter.isNotEmpty()) { filter in it.compareLabel }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun makeOpenByDefaultSettingsIntent(activityInfo: DisplayActivityInfo): Intent {
        val intent = if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
            // S*msung moment lol 🤮 (https://stackoverflow.com/a/72365164)
            Intent("android.settings.MANAGE_DOMAIN_URLS")
        } else {
            Intent(
                Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                Uri.parse("package:${activityInfo.packageName}").also { Timber.d("$it") }
            )
        }

        Timber.tag("Lel").d("$intent")
        return intent
    }
}
