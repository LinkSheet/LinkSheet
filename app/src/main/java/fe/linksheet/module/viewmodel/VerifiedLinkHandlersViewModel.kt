package fe.linksheet.module.viewmodel

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.viewModelScope
import dev.zwander.shared.IShizukuService
import fe.kotlin.extension.iterable.filterIf
import fe.kotlin.extension.iterable.groupByNoNullKeys
import fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers.FilterState
import fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers.HostState
import fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers.SortByState
import fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers.VlhSort
import fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers.VlhStateModeFilter
import fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers.VlhTypeFilter
import fe.linksheet.extension.android.SYSTEM_APP_FLAGS
import fe.linksheet.extension.kotlin.ProduceSideEffect
import fe.linksheet.extension.kotlin.mapProducingSideEffects
import fe.linksheet.feature.app.AppInfo
import fe.linksheet.feature.app.DomainVerificationAppInfo
import fe.linksheet.feature.app.LinkHandling
import fe.linksheet.feature.app.PackageService
import fe.linksheet.module.app.toPreferredApp
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.devicecompat.oneui.OneUiCompat
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.shizuku.ShizukuCommand
import fe.linksheet.module.shizuku.ShizukuServiceConnection
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Comparator
import kotlin.collections.filter

class VerifiedLinkHandlersViewModel(
    private val shizukuHandler: ShizukuServiceConnection,
    preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val packageInfoService: PackageService,
    private val intentCompat: OneUiCompat,
) : BaseViewModel(preferenceRepository) {
    val newVlh = experimentRepository.asViewModelState(Experiments.newVlh)

    val lastEmitted = MutableStateFlow(0L)

    val filterDisabledOnly = MutableStateFlow(true)

    val sortState = MutableStateFlow(SortByState(VlhSort.AZ, true))
    val filterState = MutableStateFlow(FilterState(VlhStateModeFilter.ShowAll, VlhTypeFilter.All, true))
    val searchQuery = MutableStateFlow("")

    private fun groupHosts(
        preferredApps: List<PreferredApp>,
        sideEffect: ProduceSideEffect<String>,
    ): Map<String, Collection<String>> {
        return preferredApps.groupByNoNullKeys(
            keySelector = { preferredApp ->
                preferredApp.pkg

//                with(packageInfoService) {
//                    getLauncherOrNull(preferredApp.pkg)?.let { toAppInfo(it, false) }
//                }
            },
            nullKeyHandler = { app -> app.pkg?.let { sideEffect(it) } },
            cacheIndexSelector = { it.pkg },
            valueTransform = { it.host }
        )
    }

    val preferredApps = preferredAppRepository.getAllAlwaysPreferred()
        .mapProducingSideEffects(
            sideEffectContext = Dispatchers.IO,
            transform = ::groupHosts,
            handleSideEffects = { packageNames -> preferredAppRepository.deleteByPackageNames(packageNames.toSet()) }
        )
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            replay = 1
        )

    //    private fun test(): Flow<List<DomainVerificationAppInfo>> {
//        return flowOfLazy {
//            packageInfoService.getDomainVerificationAppInfos()
//        }


    //        val appsFiltered = packageInfoService.getDomainVerificationAppInfoFlow()
//        .scan(emptyList<DomainVerificationAppInfo>()) { acc, elem -> acc + elem }
    val appsFiltered = packageInfoService.getDomainVerificationAppInfoListFlow()
        .flowOn(Dispatchers.IO)
//        .flowOn(Dispatchers.IO)
        .combine(filterState) { apps, state ->
            apps.filter { state.matches(it) }
        }
        .combine(searchQuery) { apps, searchQuery ->
            apps.filterIf(searchQuery.isNotEmpty()) { it.matches(searchQuery) }
        }
        .combine(sortState) { apps, state ->
            apps.sortedWith(state.toComparator())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = null
//            initialValue = emptyList()
        )

    private val installTime = compareBy<DomainVerificationAppInfo> { it.installTime }
    private val sortComparators = mapOf(
        VlhSort.InstallTime to (installTime to installTime.reversed()),
        VlhSort.AZ to (AppInfo.labelComparator to AppInfo.labelComparator.reversed()),
    )

    private fun SortByState.toComparator(): Comparator<DomainVerificationAppInfo> {
        val (asc, desc) = sortComparators[sort]!!
        @Suppress("UNCHECKED_CAST")
        return (if (ascending) asc else desc) as Comparator<DomainVerificationAppInfo>
    }

    private fun VlhStateModeFilter.matches(info: DomainVerificationAppInfo): Boolean {
        return when (this) {
            VlhStateModeFilter.ShowAll -> true
            VlhStateModeFilter.EnabledOnly -> info.enabled
            VlhStateModeFilter.DisabledOnly -> !info.enabled
        }
    }

    private fun VlhTypeFilter.matches(info: DomainVerificationAppInfo): Boolean {
        return when (this) {
            VlhTypeFilter.All -> true
            VlhTypeFilter.Browser -> info.linkHandling == LinkHandling.Browser
            VlhTypeFilter.Native -> info.linkHandling != LinkHandling.Browser
        }
    }

    private fun FilterState.matches(info: DomainVerificationAppInfo): Boolean {
        if (!systemApps && info.flags in SYSTEM_APP_FLAGS) return false
        if (!mode.matches(info)) return false
        if (!type.matches(info)) return false
        return true
    }

    fun emitLatest() {
        lastEmitted.value = System.currentTimeMillis()
    }

    fun search(query: String?) {
        searchQuery.value = query ?: ""
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun makeOpenByDefaultSettingsIntent(packageName: String): Intent {
        return intentCompat.createAppOpenByDefaultSettingsIntent(packageName)
    }

    fun <T> postShizukuCommand(delay: Long, command: IShizukuService.() -> T) {
        val cmd = ShizukuCommand(command) {
            viewModelScope.launch {
                delay(delay)
                emitLatest()
            }
        }

        shizukuHandler.enqueueCommand(cmd)
    }

    fun updateHostState(
        appInfo: AppInfo,
        hostStates: List<HostState>,
    ) = viewModelScope.launch(Dispatchers.IO) {
        for ((host, previousState, currentState) in hostStates) {
            when {
                previousState && !currentState -> {
                    preferredAppRepository.deleteByHostAndPackageName(host, appInfo.packageName)
                }

                !previousState && currentState -> {
                    preferredAppRepository.insert(appInfo.toPreferredApp(host, true))
                }
            }
        }
    }
}


