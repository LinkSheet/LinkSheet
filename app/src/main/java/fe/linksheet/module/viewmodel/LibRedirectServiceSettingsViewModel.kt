package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import fe.libredirectkt.LibRedirect
import fe.libredirectkt.LibRedirectFrontend
import fe.libredirectkt.LibRedirectLoader
import fe.linksheet.LibRedirectServiceRoute
import fe.linksheet.data.entity.LibRedirectDefault
import fe.linksheet.data.entity.LibRedirectServiceState
import fe.linksheet.extension.ioLaunch
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import fe.linksheet.module.viewmodel.base.SavedStateViewModel
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

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

    val builtinInstances = flowOfLazy {
        LibRedirectLoader.loadBuiltInInstances()
    }

    val default = defaultRepository.getByServiceKey(serviceKey)
        .combine(serviceKey) { default, _ -> default }

    val selectedFrontend = MutableStateFlow<LibRedirectFrontend?>(null)
    val selectedInstance = MutableStateFlow<String?>(null)

    val enabled = stateRepository.getServiceState(serviceKey.value)
        .combine(serviceKey) { serviceState, _ -> serviceState?.enabled ?: false }

    private val defaultSelectedState = service.combine(default) { service, default ->
        val frontend = if (default != null) {
            service?.frontends?.find { it.key == default.frontendKey }
        } else service?.defaultFrontend

        val instance = default?.instanceUrl ?: frontend?.defaultInstance()
        SelectedState(frontend, instance)
    }

    private fun LibRedirectFrontend.defaultInstance() = LibRedirect
        .getDefaultInstanceForFrontend(key)?.firstOrNull()

    data class SelectedState(val frontend: LibRedirectFrontend?, val instance: String?)

    init {
        ioLaunch {
            defaultSelectedState.collect {
                selectedFrontend.value = it.frontend
                selectedInstance.value = it.instance
            }
        }
    }

    fun updateSelectedFrontend(frontend: LibRedirectFrontend) {
        selectedFrontend.value = frontend
        selectedInstance.value = frontend.defaultInstance()
    }


    val instances = builtinInstances.combine(selectedFrontend) { instances, selected ->
        instances.find { it.frontendKey == selected?.key }?.hosts
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


//    val instance = if(default != null) default.instanceUrl else


//    LaunchedEffect(viewModel.libRedirectDefault) {
//        val default = viewModel.libRedirectDefault
//        Timber.tag("LibRedirect").d("Default $default")
//        if (default != null) {
//            selectedFrontend =
//                fe.linksheet.composable.settings.link.libRedirectBuiltInServices.find { it.key == serviceKey }?.frontends?.find { it.key == default.frontendKey }
//            selectedInstance = default.instanceUrl
//        } else {
//            selectedFrontend =
//                fe.linksheet.composable.settings.link.libRedirectBuiltInServices.find { it.key == serviceKey }?.defaultFrontend
//            selectedInstance =
//                LibRedirect.getDefaultInstanceForFrontend(selectedFrontend?.key!!)?.firstOrNull()
//        }
//
//        instancesForSelectedFrontend.clear()
//        builtinInstances.find { it.frontendKey == selectedFrontend?.key }?.let {
//            instancesForSelectedFrontend.addAll(it.hosts)
//        }
//    }

}