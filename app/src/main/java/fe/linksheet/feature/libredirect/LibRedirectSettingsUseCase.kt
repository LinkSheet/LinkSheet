package fe.linksheet.feature.libredirect

import fe.libredirectkt.LibRedirectLoader
import fe.libredirectkt.LibRedirectService
import fe.linksheet.module.database.entity.LibRedirectDefault
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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
    val defaultFrontend: FrontendState?
) {
    fun maybeGetFrontend(frontendKey: String): FrontendState? {
        return frontends.firstOrNull { it.frontendKey == frontendKey }
    }
}

class LibRedirectSettingsUseCase(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    suspend fun loadSettings(serviceKey: String) = withContext(ioDispatcher) {
        val builtInInstances = LibRedirectLoader.loadBuiltInInstances().associate { it.frontendKey to it.hosts.toSet() }

        val service = createService(serviceKey) ?: return@withContext null
        val (states, defaultFrontend) = loadInstances(service, builtInInstances)
        val fallback = createFallback(service, builtInInstances)

        ServiceSettings(service, fallback, states, defaultFrontend)
    }

    private fun createService(serviceKey: String): LibRedirectService? {
        val builtInServices = LibRedirectLoader.loadBuiltInServices().associateBy { it.key }

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
            val instances = builtInFrontendInstances[frontend.key]
            if (instances.isNullOrEmpty()) continue

            val state = FrontendState(service.key, frontend.key, frontend.name, instances, instances.first())
            if(frontend.key == defaultFrontendKey) {
                fallback = state
            }

            states.add(state)
        }

        return states to fallback
    }

    private fun createFallback(
        service: LibRedirectService,
        builtInFrontendInstances: Map<String, Set<String>>
    ): LibRedirectDefault? {
        val defaultFrontendKey = service.defaultFrontend.key
        val instances = builtInFrontendInstances[defaultFrontendKey]
        if (instances.isNullOrEmpty()) return null

        return LibRedirectDefault(
            service.key,
            defaultFrontendKey,
            instances.first()
        )
    }
}
