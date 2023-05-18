package fe.linksheet.composable.settings.apps.link

import android.content.Context
import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.extension.filterIf
import fe.linksheet.extension.getDisplayActivityInfos
import fe.linksheet.extension.hasVerifiedDomains
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class AppsWhichCanOpenLinksViewModel : ViewModel(), KoinComponent {
    private val context by inject<Context>()

    @RequiresApi(Build.VERSION_CODES.S)
    private val domainVerificationManager = context.getSystemService<DomainVerificationManager>()!!

    val linkHandlingAllowed = MutableStateFlow(true)
    val searchFilter = MutableStateFlow("")

    @RequiresApi(Build.VERSION_CODES.S)
    val apps = flow {
        emit(domainVerificationManager.getDisplayActivityInfos(context))
    }.combine(
        linkHandlingAllowed
    ) { apps, _ ->
        apps.filter {
            it.resolvedInfo.hasVerifiedDomains(domainVerificationManager, linkHandlingAllowed.value)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    val appsFiltered = apps.combine(searchFilter) { apps, filter ->
        apps.filterIf(filter.isNotEmpty()) { it.compareLabel.contains(filter, ignoreCase = true) }
            .toList()
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

        return intent
    }
}
