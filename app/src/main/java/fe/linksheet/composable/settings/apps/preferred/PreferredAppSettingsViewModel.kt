package fe.linksheet.composable.settings.apps.preferred

import android.content.Context
import android.content.pm.verify.domain.DomainVerificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.extension.filterIf
import fe.linksheet.extension.getAppHosts
import fe.linksheet.extension.getDisplayActivityInfos
import fe.linksheet.extension.groupBy
import fe.linksheet.extension.ioAsync
import fe.linksheet.extension.ioLaunch
import fe.linksheet.extension.mapToSet
import fe.linksheet.module.database.repository.PreferredAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PreferredAppSettingsViewModel : ViewModel(), KoinComponent {
    private val context by inject<Context>()
    private val repository by inject<PreferredAppRepository>()

    @RequiresApi(Build.VERSION_CODES.S)
    private val domainVerificationManager = context.getSystemService<DomainVerificationManager>()!!

    val searchFilter = MutableStateFlow("")

    private val preferredApps = repository.getAllAlwaysPreferred().map { app ->
        app.groupBy(
            keySelector = { it.toDisplayActivityInfo(context) },
            valueTransform = { it.host }
        ).toList().sortedBy { it.first.compareLabel }
    }

    val preferredAppsFiltered = preferredApps.combine(searchFilter) { apps, filter ->
        apps.filterIf(filter.isNotEmpty()) { (info, _) ->
            info.compareLabel.contains(filter, ignoreCase = true)
        }.toList()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    val appsExceptPreferred = preferredApps.map { app ->
        val preferredAppsPackage = app.mapToSet { it.first.packageName }
        domainVerificationManager.getDisplayActivityInfos(context, true) {
            it.activityInfo.packageName !in preferredAppsPackage
        }
    }

    fun getHostStateAsync(
        displayActivityInfo: DisplayActivityInfo,
        hosts: MutableCollection<String>
    ) = ioAsync {
        val hostState = mutableMapOf<String, Boolean>()
        val hasAppHosts = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val appHosts = domainVerificationManager.getAppHosts(displayActivityInfo.packageName)
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
