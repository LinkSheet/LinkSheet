package app.linksheet.feature.libredirect

import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import fe.libredirectkt.LibRedirectInstance
import fe.libredirectkt.LibRedirectLoader
import fe.libredirectkt.LibRedirectService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LibRedirectSettingsFeature(
    private val loadBuiltInServices: () -> List<LibRedirectService> = LibRedirectLoader::loadBuiltInServices,
    private val loadBuiltInInstances: () -> List<LibRedirectInstance> = LibRedirectLoader::loadBuiltInInstances,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun loadSettings(serviceKey: String) = withContext(ioDispatcher) {
        val builtInInstances = loadBuiltInInstances().associate { it.frontendKey to it.hosts.toSet() }

        val service = createService(serviceKey) ?: return@withContext null
        val (states, defaultFrontendState) = loadInstances(service, builtInInstances)
        val fallback = defaultFrontendState?.let { createDefault(it) }

        ServiceSettings(service, fallback, states, defaultFrontendState)
    }

    private fun createService(serviceKey: String): LibRedirectService? {
        val builtInServices = loadBuiltInServices().associateBy { it.key }

        val service = builtInServices[serviceKey]
        if (service == null) return null

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
}
