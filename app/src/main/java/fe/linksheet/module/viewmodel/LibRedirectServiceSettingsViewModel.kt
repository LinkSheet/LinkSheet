package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import fe.libredirectkt.LibRedirectLoader
import fe.libredirectkt.LibRedirectService
import fe.linksheet.navigation.LibRedirectServiceRoute
import fe.linksheet.extension.android.launchIO
import fe.linksheet.extension.kotlin.mapProducingSideEffect
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.database.entity.LibRedirectServiceState
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import fe.linksheet.module.viewmodel.base.SavedStateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class BuiltInFrontendHolder(
    val key: String,
    val name: String,
    val instances: Set<String>,
    val defaultInstance: String,
)

class LibRedirectServiceSettingsViewModel(
    val context: Application,
    savedStateHandle: SavedStateHandle,
    private val defaultRepository: LibRedirectDefaultRepository,
    private val stateRepository: LibRedirectStateRepository,
    preferenceRepository: AppPreferenceRepository,
) : SavedStateViewModel<LibRedirectServiceRoute>(savedStateHandle, preferenceRepository) {
    private val serviceKey = getSavedState(LibRedirectServiceRoute::serviceKey)!!

    companion object {
        val builtInServices: Map<String, LibRedirectService> by lazy {
            LibRedirectLoader.loadBuiltInServices().associateBy { it.key }
        }

        val builtInFrontendInstances: Map<String, Set<String>> by lazy {
            LibRedirectLoader.loadBuiltInInstances().associate { it.frontendKey to it.hosts.toSet() }
        }
    }

    private val frontends by lazy {
        builtInServices[serviceKey]!!.frontends.mapNotNull {
            val inst = builtInFrontendInstances[it.key]
            if (inst.isNullOrEmpty()) return@mapNotNull null

            it.key to BuiltInFrontendHolder(it.key, it.name, inst, inst.first())
        }.toMap()
    }

    val service: LibRedirectService by lazy { builtInServices[serviceKey]!! }
    private val fallback: LibRedirectDefault by lazy {
        LibRedirectDefault(
            serviceKey,
            service.defaultFrontend.key,
            frontends[service.defaultFrontend.key]!!.defaultInstance
        )
    }

    fun getFrontendByKey(key: String): BuiltInFrontendHolder? {
        return frontends[key]
    }

    fun getFrontends(): Iterable<BuiltInFrontendHolder> {
        return frontends.values
    }

    fun getInstancesFor(frontendKey: String?): Set<String> {
        if(frontendKey == null) return emptySet()
        return frontends[frontendKey]?.instances ?: emptySet()
    }

    val selected: Flow<LibRedirectDefault> = defaultRepository
        .getByServiceKey(serviceKey)
        .mapProducingSideEffect(
            transform = { stored, delete ->
                if (stored == null) return@mapProducingSideEffect fallback

                val instances = getInstancesFor(stored.frontendKey)
                if (instances == null) {
                    delete(stored)
                    return@mapProducingSideEffect fallback
                }

                if (stored.instanceUrl != LibRedirectDefault.randomInstance && stored.instanceUrl !in instances) {
                    // TODO: Can we update this?
                    return@mapProducingSideEffect fallback
//                     TODO: Do something about this
//                    return@mapProducingSideEffect stored.copy(instanceUrl = fallback.instanceUrl)
                }

                return@mapProducingSideEffect stored
            },
            handleSideEffect = { default ->
                withContext(Dispatchers.IO) { defaultRepository.delete(default) }
            }
        )

    val enabled = stateRepository.isEnabledFlow(serviceKey)

    fun updateInstance(default: LibRedirectDefault, instance: String): Job {
        return launchIO { defaultRepository.insert(default.copy(instanceUrl = instance)) }
    }

    fun updateFrontend(default: LibRedirectDefault, frontendKey: String): Job {
        return launchIO {
            defaultRepository.insert(
                default.copy(
                    frontendKey = frontendKey,
                    instanceUrl = frontends[frontendKey]!!.defaultInstance
                )
            )
        }
    }

    fun updateState(enabled: Boolean): Job {
        return launchIO { stateRepository.insert(LibRedirectServiceState(serviceKey, enabled)) }
    }
}
