package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import fe.libredirectkt.LibRedirect
import fe.libredirectkt.LibRedirectFrontend
import fe.libredirectkt.LibRedirectLoader
import fe.linksheet.LibRedirectServiceRoute
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.database.entity.LibRedirectServiceState
import fe.linksheet.extension.android.ioLaunch
import fe.android.preference.helper.PreferenceRepository
import fe.libredirectkt.LibRedirectInstance
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import fe.linksheet.module.viewmodel.base.SavedStateViewModel
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.toCollection

class LibRedirectServiceSettingsViewModel(
    val context: Application,
    savedStateHandle: SavedStateHandle,
    private val defaultRepository: LibRedirectDefaultRepository,
    private val stateRepository: LibRedirectStateRepository,
    preferenceRepository: PreferenceRepository
) : SavedStateViewModel<LibRedirectServiceRoute>(savedStateHandle, preferenceRepository) {
    val serviceKey = MutableStateFlow(getSavedState(LibRedirectServiceRoute::serviceKey)!!)

    val service = flowOfLazy {
        LibRedirectLoader.loadBuiltInServices()
    }.combine(serviceKey) { services, key -> services.find { it.key == key } }

    private val builtinInstances = flowOfLazy {
        LibRedirectLoader.loadBuiltInInstances()
    }

    val frontends = service.combine(builtinInstances) { service, instances ->
        service?.frontends?.filter { it.defaultInstance(instances) != null } ?: emptyList()
    }

    val default = defaultRepository.getByServiceKeyFlow(serviceKey)
        .combine(serviceKey) { default, _ -> default }

    val selectedFrontend = MutableStateFlow<LibRedirectFrontend?>(null)
    val selectedInstance = MutableStateFlow<String?>(null)

    val instancesForSelected = builtinInstances.combine(selectedFrontend) { instances, selected ->
        instances.find { it.frontendKey == selected?.key }?.hosts?.sorted()
    }

    val enabled = stateRepository.isEnabledFlow(serviceKey.value)
        .combine(serviceKey) { serviceState, _ -> serviceState }

    private val defaultSelectedState = service.combine(default) { service, default ->
        val frontend = if (default != null) {
            service?.frontends?.find { it.key == default.frontendKey }
        } else service?.defaultFrontend

        frontend to default
    }.combine(builtinInstances) { pair, instances ->
        val (frontend, default) = pair
        val instance = default?.instanceUrl ?: frontend?.defaultInstance(instances)
        SelectedState(frontend, instance)
    }

    private lateinit var instances: List<LibRedirectInstance>

    private fun LibRedirectFrontend.defaultInstance(instances: List<LibRedirectInstance>) =
        LibRedirect
            .getDefaultInstanceForFrontend(key, instances)

    data class SelectedState(val frontend: LibRedirectFrontend?, val instance: String?)

    init {
        ioLaunch {
            defaultSelectedState.collect {
                selectedFrontend.value = it.frontend
                selectedInstance.value = it.instance
            }

            builtinInstances.collectLatest {
                instances = it
            }
        }
    }

    fun updateSelectedFrontend(frontend: LibRedirectFrontend) {
        selectedFrontend.value = frontend
        selectedInstance.value = frontend.defaultInstance(instances)
    }

    fun updateLibRedirectState(serviceKey: String, enabled: Boolean) = ioLaunch {
        stateRepository.insert(LibRedirectServiceState(serviceKey, enabled))
    }

    fun saveLibRedirectDefault(
        serviceKey: String,
        frontendKey: String,
        instanceUrl: String
    ) = ioLaunch {
        defaultRepository.insert(LibRedirectDefault(serviceKey, frontendKey, instanceUrl))
    }
}