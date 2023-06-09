package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.extension.filterIf
import fe.linksheet.extension.compose.getDisplayActivityInfos
import fe.linksheet.extension.android.hasVerifiedDomains
import fe.android.preference.helper.PreferenceRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class AppsWhichCanOpenLinksViewModel(
    val context: Application,
    preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {

    @RequiresApi(Build.VERSION_CODES.S)
    private val domainVerificationManager = context.getSystemService<DomainVerificationManager>()!!

    val linkHandlingAllowed = MutableStateFlow(true)
    val searchFilter = MutableStateFlow("")

    @RequiresApi(Build.VERSION_CODES.S)
    private val apps = flowOfLazy { domainVerificationManager.getDisplayActivityInfos(context) }.combine(
        linkHandlingAllowed
    ) { apps, _ ->
        apps.filter {
            it.resolvedInfo.hasVerifiedDomains(domainVerificationManager, linkHandlingAllowed.value)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    val appsFiltered = apps.combine(searchFilter) { apps, filter ->
        apps.filterIf(filter.isNotEmpty()) { it.compareLabel.contains(filter, ignoreCase = true) }
            .sortedWith(DisplayActivityInfo.labelComparator)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun makeOpenByDefaultSettingsIntent(activityInfo: DisplayActivityInfo): Intent {
        val intent = if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
            // S*msung moment lol (https://stackoverflow.com/a/72365164)
            Intent("android.settings.MANAGE_DOMAIN_URLS")
        } else {
            Intent(
                Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                Uri.parse("package:${activityInfo.packageName}")
            )
        }

        return intent
    }
}
