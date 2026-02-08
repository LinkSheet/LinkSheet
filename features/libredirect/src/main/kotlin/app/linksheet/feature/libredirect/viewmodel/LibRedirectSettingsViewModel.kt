package app.linksheet.feature.libredirect.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.libredirect.database.repository.LibRedirectDefaultRepository
import app.linksheet.feature.libredirect.database.repository.LibRedirectStateRepository
import app.linksheet.feature.libredirect.preference.LibRedirectPreferences
import fe.libredirectkt.LibRedirectInstance
import fe.libredirectkt.LibRedirectLoader
import fe.libredirectkt.LibRedirectService
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class LibRedirectSettingsViewModel(
    val context: Application,
    private val defaultRepository: LibRedirectDefaultRepository,
    private val stateRepository: LibRedirectStateRepository,
    preferenceRepository: AppPreferenceRepository,
    libRedirectPreferences: LibRedirectPreferences,
) : ViewModel() {
    companion object {
        private val comparator = compareByDescending<LibRedirectServiceWithInstance> { (_, enabled) ->
            enabled
        }.thenBy { (service) -> service.name }
    }

    val enableLibRedirect = preferenceRepository.asViewModelState(libRedirectPreferences.enable)

    private val builtInServices = flowOfLazy { LibRedirectLoader.loadBuiltInServices() }
    private val builtinInstances = flowOfLazy {
        LibRedirectLoader.loadBuiltInInstances()
    }

    val services = builtInServices
        .combine(builtinInstances, ::buildState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = null
//            initialValue = emptyList()
        )

    data class LibRedirectServiceWithInstance(
        val service: LibRedirectService,
        val enabled: Boolean,
        val instance: String?
    )

    private suspend fun buildState(
        services: List<LibRedirectService>,
        instances: List<LibRedirectInstance>
    ): List<LibRedirectServiceWithInstance> {
        return services
            .filter { service -> hasAtLeastOneFrontendWithOneInstance(service, instances) }
            .map { service ->
                val enabled = stateRepository.isEnabled(service.key)
                LibRedirectServiceWithInstance(
                    service = service,
                    enabled = enabled,
                    instance = when {
                        enabled -> defaultRepository.getInstanceUrl(service.key)
                        else -> null
                    }
                )
            }
            .sortedWith(comparator)
    }

    private fun hasAtLeastOneFrontendWithOneInstance(
        service: LibRedirectService,
        instances: List<LibRedirectInstance>
    ): Boolean {
        for (frontend in service.frontends) {
            if (instances.find { it.frontendKey == frontend.key }?.hosts?.isNotEmpty() == true) {
                return true
            }
        }

        return false
    }
}
