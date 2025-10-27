@file:OptIn(RefactorGlue::class)

package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.pm.verify.domain.DomainVerificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.lifecycle.viewModelScope
import fe.composekit.core.AndroidVersion
import fe.kotlin.extension.iterable.groupByNoNullKeys
import fe.kotlin.extension.iterable.mapToSet
import fe.kotlin.extension.map.filterIf
import fe.linksheet.extension.android.ioAsync
import fe.linksheet.extension.android.launchIO
import fe.linksheet.extension.compose.getAppHosts
import fe.linksheet.extension.compose.getDisplayActivityInfos
import fe.linksheet.extension.kotlin.ProduceSideEffect
import fe.linksheet.extension.kotlin.mapProducingSideEffects
import fe.linksheet.feature.app.ActivityAppInfo
import fe.linksheet.feature.app.PackageService
import fe.linksheet.module.app.ActivityAppInfoGlue
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.resolver.DisplayActivityInfo
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.RefactorGlue
import fe.linksheet.web.VerifiedDomainService.canHandleDomains
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class PreferredAppSettingsViewModel(
    val context: Application,
    private val repository: PreferredAppRepository,
    preferenceRepository: AppPreferenceRepository,
    private val packageInfoService: PackageService,
) : BaseViewModel(preferenceRepository) {

    private val domainVerificationManager by lazy {
        if (AndroidVersion.isAtLeastApi31S()) {
            context.getSystemService<DomainVerificationManager>()
        } else null
    }

    val searchFilter = MutableStateFlow("")

    private val preferredApps = repository.getAllAlwaysPreferred().mapProducingSideEffects(
        transform = { app, sideEffect: ProduceSideEffect<String> ->
            app.groupByNoNullKeys(
                keySelector = {
                    packageInfoService.getLauncherOrNull(it.pkg)
                        ?.let { packageInfoService.toAppInfo(it, false) }
                },
                // TODO: Fix
                nullKeyHandler = { sideEffect("") },
                cacheIndexSelector = { it },
                valueTransform = { it.host }
            )
        },
        handleSideEffects = {
            withContext(Dispatchers.IO) { repository.deleteByPackageNames(it.toSet()) }
        }
    ).shareIn(viewModelScope, started = SharingStarted.Lazily, replay = 1)

    val preferredAppsFiltered = preferredApps.combine(searchFilter) { apps, filter ->
        apps.filterIf(condition = filter.isNotEmpty()) { (info, _) ->
            info.compareLabel.contains(filter, ignoreCase = true)
        }
    }.map { apps ->
        apps.map { (info, state) ->
            ActivityAppInfoGlue.toDisplayActivityInfo(info) to state
        }.toMap()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    val appsExceptPreferred = preferredApps.map { apps ->
        val preferredAppsPackages = apps.keys.mapToSet { it.packageName }
        domainVerificationManager!!.getDisplayActivityInfos(context) {
            it.canHandleDomains(domainVerificationManager!!) && it.activityInfo.packageName !in preferredAppsPackages
        }.sortedWith(ActivityAppInfo.labelComparator)
    }.map { apps ->
        apps.map { ActivityAppInfoGlue.toDisplayActivityInfo(it) }
    }

    fun getHostStateAsync(
        displayActivityInfo: DisplayActivityInfo,
        hosts: Collection<String>,
    ) = ioAsync {
        val hostState = mutableMapOf<String, Boolean>()
        val hasAppHosts = if (AndroidVersion.isAtLeastApi31S()) {
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
        val hostState: MutableMap<String, Boolean>,
    )

    fun updateHostState(
        displayActivityInfo: DisplayActivityInfo,
        hostState: MutableMap<String, Boolean>,
    ) = launchIO {
        hostState.forEach { (host, enabled) ->
            if (enabled) repository.insert(
                displayActivityInfo.toPreferredApp(host, true)
            )
            else repository.deleteByHostAndPackageName(host, displayActivityInfo.packageName)
        }
    }

    fun deletePreferredAppWherePackage(displayActivityInfo: DisplayActivityInfo) = launchIO {
        repository.deleteByPackageName(displayActivityInfo.packageName)
    }

    fun insertHostState(
        displayActivityInfo: DisplayActivityInfo,
        hostState: MutableMap<String, Boolean>,
    ) = launchIO {
        repository.insert(hostState.map { (host, _) ->
            displayActivityInfo.toPreferredApp(host, true)
        })
    }
}
