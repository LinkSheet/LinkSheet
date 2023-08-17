package fe.linksheet.module.viewmodel

import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.kotlin.extension.filterIf
import fe.linksheet.LinkSheetApp
import fe.linksheet.extension.compose.getDisplayActivityInfos
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.VerifiedDomainUtil.hasVerifiedDomains
import fe.linksheet.util.flowOfLazy
import fe.linksheet.util.getAppOpenByDefaultIntent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class AppsWhichCanOpenLinksViewModel(
    val app: LinkSheetApp,
    preferenceRepository: PreferenceRepository,
    featureFlagRepository: PreferenceRepository,
) : BaseViewModel(preferenceRepository) {

    val featureFlagShizuku = featureFlagRepository.getBooleanState(Preferences.featureFlagShizuku)

    private val domainVerificationManager by lazy {
        if (AndroidVersion.AT_LEAST_API_31_S) {
            app.getSystemService<DomainVerificationManager>()
        } else null
    }

    val refreshing = MutableStateFlow(false)
    val linkHandlingAllowed = MutableStateFlow(true)
    val searchFilter = MutableStateFlow("")

    @RequiresApi(Build.VERSION_CODES.S)
    private val apps = flowOfLazy { domainVerificationManager!!.getDisplayActivityInfos(app) }
        .combine(refreshing) { apps, _ ->
            refreshing.value = false
            apps
        }
        .combine(linkHandlingAllowed) { apps, _ ->
            apps.filter {
                it.resolvedInfo.hasVerifiedDomains(
                    domainVerificationManager!!,
                    linkHandlingAllowed.value
                )
            }
        }

    @RequiresApi(Build.VERSION_CODES.S)
    val appsFiltered = apps.combine(searchFilter) { apps, filter ->
        apps.filterIf(filter.isNotEmpty()) { it.compareLabel.contains(filter, ignoreCase = true) }
            .sortedWith(DisplayActivityInfo.labelComparator)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun makeOpenByDefaultSettingsIntent(activityInfo: DisplayActivityInfo): Intent {
        return getAppOpenByDefaultIntent(activityInfo.packageName)
    }
}
