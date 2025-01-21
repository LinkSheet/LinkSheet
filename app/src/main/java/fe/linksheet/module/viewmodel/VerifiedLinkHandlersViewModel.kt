package fe.linksheet.module.viewmodel

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAltOff
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import dev.zwander.shared.IShizukuService
import fe.kotlin.extension.iterable.filterIf
import fe.kotlin.extension.iterable.groupByNoNullKeys
import fe.linksheet.R
import fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers.HostState
import fe.linksheet.extension.android.SYSTEM_APP_FLAGS
import fe.linksheet.extension.kotlin.ProduceSideEffect
import fe.linksheet.extension.kotlin.mapProducingSideEffects
import fe.linksheet.module.app.AppInfo
import fe.linksheet.module.app.DomainVerificationAppInfo
import fe.linksheet.module.app.PackageService
import fe.linksheet.module.app.toPreferredApp
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.devicecompat.samsung.SamsungIntentCompat
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.shizuku.ShizukuCommand
import fe.linksheet.module.shizuku.ShizukuHandler
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerifiedLinkHandlersViewModel(
    private val shizukuHandler: ShizukuHandler,
    preferenceRepository: AppPreferenceRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val packageInfoService: PackageService,
    private val intentCompat: SamsungIntentCompat
) : BaseViewModel(preferenceRepository) {

    val lastEmitted = MutableStateFlow(0L)

    val filterDisabledOnly = MutableStateFlow(true)

    val userAppFilter = MutableStateFlow(true)
    val filterMode = MutableStateFlow<FilterMode>(FilterMode.ShowAll)
    val searchQuery = MutableStateFlow("")
    private val sorting = MutableStateFlow(AppInfo.labelComparator)

    private fun groupHosts( preferredApps: List<PreferredApp>, sideEffect: ProduceSideEffect<String>): Map<String, Collection<String>> {
        return preferredApps.groupByNoNullKeys(
            keySelector = { preferredApp -> preferredApp.pkg

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
            transform = ::groupHosts,
            handleSideEffects = { packageNames ->
                withContext(Dispatchers.IO) { preferredAppRepository.deleteByPackageNames(packageNames.toSet()) }
            }
        )
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            replay = 1
        )

    val appsFiltered = packageInfoService.getDomainVerificationAppInfos()
        .scan(emptyList<DomainVerificationAppInfo>()) { acc, elem -> acc + elem }
        .flowOn(Dispatchers.IO)
        .combine(userAppFilter) { apps, userAppFilter ->
            apps.filter { !userAppFilter || it.flags !in SYSTEM_APP_FLAGS }
        }
        .combine(filterMode) { apps, filterMode ->
            if (filterMode == FilterMode.ShowAll) return@combine apps

            val enabledMode = filterMode == FilterMode.EnabledOnly
            apps.filter { if (enabledMode) it.enabled else !it.enabled }
        }
        .combine(searchQuery) { apps, searchQuery ->
            apps.filterIf(searchQuery.isNotEmpty()) { it.matches(searchQuery) }
        }
        .combine(sorting) { apps, sorting ->
            apps.sortedWith(sorting)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = emptyList()
        )

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

sealed class FilterMode(
    @StringRes val shortStringRes: Int,
    @StringRes val stringRes: Int,
    val icon: ImageVector,
) {

    data object ShowAll : FilterMode(
        R.string.settings_verified_link_handlers__text_handling_filter_all_short,
        R.string.settings_verified_link_handlers__text_handling_filter_all_short,
        Icons.Outlined.FilterAltOff
    )

    data object EnabledOnly : FilterMode(
        R.string.settings_verified_link_handlers__text_handling_filter_enabled_short,
        R.string.settings_verified_link_handlers__text_handling_filter_enabled_short,
        Icons.Outlined.Visibility
    )

    data object DisabledOnly : FilterMode(
        R.string.settings_verified_link_handlers__text_handling_filter_disabled_short,
        R.string.settings_verified_link_handlers__text_handling_filter_disabled_short,
        Icons.Outlined.VisibilityOff
    )

    companion object {
        val Modes by lazy { arrayOf(ShowAll, EnabledOnly, DisabledOnly) }
    }
}
