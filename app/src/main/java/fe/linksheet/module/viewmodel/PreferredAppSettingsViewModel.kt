package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.pm.verify.domain.DomainVerificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import fe.kotlin.extension.iterable.filterIf
import fe.kotlin.extension.iterable.groupByNoNullKeys
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.extension.android.ioAsync
import fe.linksheet.extension.android.ioLaunch
import fe.linksheet.extension.compose.getAppHosts
import fe.linksheet.extension.compose.getDisplayActivityInfos
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.VerifiedDomainUtil.canHandleDomains
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PreferredAppSettingsViewModel(
    val context: Application,
    private val repository: PreferredAppRepository,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {

    private val domainVerificationManager by lazy {
        if (AndroidVersion.AT_LEAST_API_31_S) {
            context.getSystemService<DomainVerificationManager>()
        } else null
    }

    val searchFilter = MutableStateFlow("")

    private val preferredApps = repository.getAllAlwaysPreferred().map { app ->
        app.groupByNoNullKeys(
            keySelector = { it.toDisplayActivityInfo(context) },
            cacheKeySelector = { it },
            valueTransform = { it.host }
        ).toList()
    }

    val preferredAppsFiltered = preferredApps.combine(searchFilter) { apps, filter ->
        apps.filterIf(filter.isNotEmpty()) { (info, _) ->
            info.compareLabel.contains(filter, ignoreCase = true)
        }.toList()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    val appsExceptPreferred = preferredApps.map { apps ->
        val preferredAppsPackages = apps.mapToSet { it.first.packageName }
        domainVerificationManager!!.getDisplayActivityInfos(context) {
            it.canHandleDomains(domainVerificationManager!!) && it.activityInfo.packageName !in preferredAppsPackages
        }.sortedWith(DisplayActivityInfo.labelComparator)
    }

    fun getHostStateAsync(
        displayActivityInfo: DisplayActivityInfo,
        hosts: Collection<String>
    ) = ioAsync {
        val hostState = mutableMapOf<String, Boolean>()
        val hasAppHosts = if (AndroidVersion.AT_LEAST_API_31_S) {
            val appHosts = domainVerificationManager!!.getAppHosts(displayActivityInfo.packageName)
            appHosts.forEach { hostState[it] = it in hosts }

            appHosts.isNotEmpty()
        } else false

        hosts.forEach { hostState[it] = true }
        HostStateResult(displayActivityInfo, hasAppHosts, hostState.toSortedMap())
    }

    data class HostStateResult(
        val displayActivityInfo: DisplayActivityInfo,
        val hasAppHosts: Boolean,
        val hostState: MutableMap<String, Boolean>
    )

    fun updateHostState(
        displayActivityInfo: DisplayActivityInfo,
        hostState: MutableMap<String, Boolean>
    ) = ioLaunch {
        hostState.forEach { (host, enabled) ->
            if (enabled) repository.insert(
                displayActivityInfo.toPreferredApp(host, true)
            )
            else repository.deleteByHostAndPackageName(host, displayActivityInfo.packageName)
        }
    }

    fun deletePreferredAppWherePackage(displayActivityInfo: DisplayActivityInfo) = ioLaunch {
        repository.deleteByPackageName(displayActivityInfo.packageName)
    }

    fun insertHostState(
        displayActivityInfo: DisplayActivityInfo,
        hostState: MutableMap<String, Boolean>
    ) = ioLaunch {
        repository.insert(hostState.map { (host, _) ->
            displayActivityInfo.toPreferredApp(host, true)
        })
    }
}
