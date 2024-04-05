package fe.linksheet.module.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService

import fe.kotlin.extension.iterable.filterIf
import fe.linksheet.extension.compose.getDisplayActivityInfos
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.shizuku.ShizukuCommand
import fe.linksheet.module.shizuku.ShizukuHandler
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.VerifiedDomainUtil.hasVerifiedDomains
import fe.linksheet.util.flowOfLazy
import fe.linksheet.util.getAppOpenByDefaultIntent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class AppsWhichCanOpenLinksViewModel(
    context: Context,
    private val shizukuHandler: ShizukuHandler,
    preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {

    private val domainVerificationManager by lazy {
        if (AndroidVersion.AT_LEAST_API_31_S) {
            context.getSystemService<DomainVerificationManager>()
        } else null
    }

    val refreshing = MutableStateFlow(false)
    val linkHandlingAllowed = MutableStateFlow(true)
    val searchFilter = MutableStateFlow("")

    @RequiresApi(Build.VERSION_CODES.S)
    private val apps = flowOfLazy { domainVerificationManager!!.getDisplayActivityInfos(context) }
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

    fun refresh() {
//        if (refreshing.value) {
//            refreshing.value = false
//        }

        refreshing.value = true
    }

    fun stopRefresh() {
        refreshing.value = false
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun makeOpenByDefaultSettingsIntent(activityInfo: DisplayActivityInfo): Intent {
        return getAppOpenByDefaultIntent(activityInfo.packageName)
    }

    fun postShizukuCommand(command: ShizukuCommand) {
        shizukuHandler.postShizukuCommand(command)
    }
}
