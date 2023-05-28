package fe.linksheet.module.viewmodel

import android.app.Application
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.libredirectkt.LibRedirectInstance
import fe.libredirectkt.LibRedirectLoader
import fe.libredirectkt.LibRedirectService
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class LibRedirectSettingsViewModel(
    val context: Application,
    private val defaultRepository: LibRedirectDefaultRepository,
    private val stateRepository: LibRedirectStateRepository,
    preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    companion object {
        private val comparator = compareByDescending<LibRedirectServiceWithInstance> { (_, enabled) ->
            enabled
        }.thenBy { (service) -> service.name }
    }

    var enableLibRedirect = preferenceRepository.getBooleanState(Preferences.enableLibRedirect)

    private val builtInServices = flowOfLazy { LibRedirectLoader.loadBuiltInServices() }
    private val builtinInstances = flowOfLazy {
        LibRedirectLoader.loadBuiltInInstances()
    }

    val services = builtInServices.combine(builtinInstances) { services, instances ->
        services.filter { service -> hasAtLeastOneFrontendWithOneInstance(service, instances) }
            .map { service ->
                val enabled = stateRepository.isEnabled(service.key)

                val instance = if (stateRepository.isEnabled(service.key)) {
                    defaultRepository.getInstanceUrl(service.key)
                } else null

                LibRedirectServiceWithInstance(
                    service,
                    enabled,
                    instance
                )
            }.sortedWith(comparator)
    }

    data class LibRedirectServiceWithInstance(
        val service: LibRedirectService,
        val enabled: Boolean,
        val instance: String?
    )

    private fun hasAtLeastOneFrontendWithOneInstance(
        service: LibRedirectService,
        instances: List<LibRedirectInstance>
    ): Boolean {
        service.frontends.forEach { frontend ->
            if (instances.find { it.frontendKey == frontend.key }?.hosts?.isNotEmpty() == true) {
                return true
            }
        }

        return false
    }
}