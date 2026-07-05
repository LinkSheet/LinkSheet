package fe.linksheet.module.viewmodel

import android.content.Intent
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.app.usecase.DomainVerificationUseCase
import app.linksheet.feature.devicecompat.oneui.OneUiCompat
import fe.composekit.core.AndroidVersion
import fe.kotlin.extension.iterable.filterIf
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.composable.dialog.HostState
import fe.linksheet.composable.dialog.createResult
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.viewmodel.common.handler.LinkHandlerCommon
import fe.linksheet.navigation.VlhAppRoute
import fe.linksheet.util.intent.StandardIntents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VerifiedLinkHandlerViewModel(
    private val handle: SavedStateHandle,
    val preferenceRepository: AppPreferenceRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val service: DomainVerificationUseCase,
    private val intentCompat: OneUiCompat,
) : ViewModel() {
    private val routeData = handle.toRoute<VlhAppRoute>()
    val appInfo = service.createDomainVerificationAppInfo(routeData.packageName)
    val preferredAppHostSetFlow = preferredAppRepository
        .getByPackageNameFlow(routeData.packageName)
        .map { preferredApps -> preferredApps.mapToSet { it.host } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = emptySet()
//            initialValue = emptyList()
        )

    private val hostSetFlow = flow {
        emit(appInfo?.hostSet)
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredHostsFlow = combine(
        flow = hostSetFlow,
        flow2 = _searchQuery
    ) { preferredHostSet, searchQuery ->
        preferredHostSet
            ?.filterIf(searchQuery.isNotEmpty()) { it.contains(searchQuery) }
            ?.sortedBy { it }
            ?.toList()
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = null
//            initialValue = emptyList()
        )


    private val handler by lazy {
        LinkHandlerCommon(preferredAppRepository = preferredAppRepository)
    }

    fun openSettings(): Intent {
        return when {
            AndroidVersion.isAtLeastApi31S() -> intentCompat.createAppOpenByDefaultSettingsIntent(
                routeData.packageName
            )

            else -> StandardIntents.createAppSettingsIntent(routeData.packageName)
        }
    }

    fun search(query: String?) {
        _searchQuery.value = query ?: ""
    }

    fun save(holder: VlhStateHolder) = viewModelScope.launch {
        if (appInfo == null) return@launch
        val result = holder.toResult()
        handler.updateHostState(appInfo, result)
    }
}

data class VlhStateHolder(
    private val hostStates: SnapshotStateMap<String, Boolean>,
    internal val initialStates: Map<String, Boolean>,
) {
    private val changedHosts = mutableStateSetOf<String>()
    val hasChanges: Boolean
        get() = changedHosts.isNotEmpty()

    fun put(host: String, newState: Boolean) {
        val initialState = initialStates[host] ?: return

        hostStates[host] = newState
        if (initialState == newState) {
            changedHosts.remove(host)
        } else {
            changedHosts.add(host)
        }
    }

    fun get(host: String): Boolean? {
        return hostStates[host]
    }

    fun reset() {
        hostStates.putAll(initialStates)
        changedHosts.clear()
    }

    fun toResult(): List<HostState> {
        return initialStates.createResult(hostStates)
    }
}
