package app.linksheet.feature.libredirect

import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import fe.libredirectkt.LibRedirectService

class SettingsController(
    private val useCase: LibRedirectUseCase,
) {
    suspend fun loadSettings(serviceKey: String): ServiceSettings? {
        val builtInInstances = useCase.loadBuiltInInstances().associate { it.frontendKey to it.hosts.toSet() }

        val service = createService(serviceKey) ?: return null
        val (states, defaultFrontendState) = loadInstances(service, builtInInstances)
        val fallback = defaultFrontendState?.let { createDefault(it) }

        return ServiceSettings(service, fallback, states, defaultFrontendState)
    }

    private suspend fun createService(serviceKey: String): LibRedirectService? {
        val builtInServices = useCase.loadBuiltInServices().associateBy { it.key }

        val service = builtInServices[serviceKey] ?: return null
        return service
    }

    private fun loadInstances(
        service: LibRedirectService,
        builtInFrontendInstances: Map<String, Set<String>>,
    ): Pair<List<FrontendState>, FrontendState?> {
        val defaultFrontendKey = service.defaultFrontend.key
        var fallback: FrontendState? = null

        val states = mutableListOf<FrontendState>()
        for (frontend in service.frontends) {
            val frontendKey = frontend.key
            val instances = builtInFrontendInstances[frontendKey]
            if (instances.isNullOrEmpty()) continue

            val state = FrontendState(service.key, frontendKey, frontend.name, instances, instances.first())
            if (frontendKey == defaultFrontendKey) {
                fallback = state
            }

            states.add(state)
        }

        if (fallback == null) {
            return states to states.firstOrNull()
        }

        return states to fallback
    }

    private fun createDefault(fallback: FrontendState): LibRedirectDefault {
        return LibRedirectDefault(
            fallback.serviceKey,
            fallback.frontendKey,
            fallback.defaultInstance
        )
    }
}

data class FrontendState(
    val serviceKey: String,
    val frontendKey: String,
    val name: String,
    val instances: Set<String>,
    val defaultInstance: String,
)

data class ServiceSettings(
    val service: LibRedirectService,
    val fallback: LibRedirectDefault?,
    val frontends: List<FrontendState>,
    val defaultFrontend: FrontendState?,
) {
    fun maybeGetFrontend(frontendKey: String): FrontendState? {
        return frontends.firstOrNull { it.frontendKey == frontendKey }
    }

    fun getCurrentVersion(): Int {
        return 1
    }
}
