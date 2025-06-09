package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import fe.linksheet.extension.kotlin.ProduceSideEffect
import fe.linksheet.extension.kotlin.mapProducingSideEffect
import fe.linksheet.feature.libredirect.FrontendState
import fe.linksheet.feature.libredirect.LibRedirectSettingsFeature
import fe.linksheet.feature.libredirect.ServiceSettings
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.database.entity.LibRedirectServiceState
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibRedirectServiceSettingsViewModel(
    val context: Application,
    private val serviceKey: String,
    private val defaultRepository: LibRedirectDefaultRepository,
    private val stateRepository: LibRedirectStateRepository,
    preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {
    private val feature = LibRedirectSettingsFeature()

    private val _settings = MutableStateFlow<ServiceSettings?>(null)
    val settings = _settings.asStateFlow()

    fun loadSettings() {
        viewModelScope.launch {
            _settings.value = feature.loadSettings(serviceKey)
        }
    }

//    fun getFrontendByKey(key: String): FrontendState? {
//        return frontends[key]
//    }
//
//    fun getFrontends(): Iterable<FrontendState> {
//        return frontends.values
//    }
//
//    fun getInstancesFor(frontendKey: String?): Set<String> {
//        if (frontendKey == null) return emptySet()
//        return frontends[frontendKey]?.instances ?: emptySet()
//    }

    suspend fun transform(
        transform: Pair<ServiceSettings, LibRedirectDefault?>,
        handleSideEffect: ProduceSideEffect<LibRedirectDefault>
    ): Pair<LibRedirectDefault, FrontendState> {
        val (settings, stored) = transform

        if (stored == null) {
            return settings.fallback!! to settings.defaultFrontend!!
        }

        val frontend = settings.maybeGetFrontend(stored.frontendKey)
        if (frontend == null) {
            handleSideEffect(stored)
            return settings.fallback!! to settings.defaultFrontend!!
        }

        if (stored.instanceUrl != LibRedirectDefault.randomInstance && stored.instanceUrl !in frontend.instances) {
            // TODO: Can we update this?
            return settings.fallback!! to settings.defaultFrontend!!
//                     TODO: Do something about this
//                    return@mapProducingSideEffect stored.copy(instanceUrl = fallback.instanceUrl)
        }

        return stored to frontend
    }


    val selectedFrontend: Flow<Pair<LibRedirectDefault, FrontendState>> = _settings
        .filterNotNull()
        .combine(defaultRepository.getByServiceKey(serviceKey)) { settings, stored -> settings to stored }
        .mapProducingSideEffect(
            transform = ::transform,
            handleSideEffect = { default ->
                withContext(Dispatchers.IO) { defaultRepository.delete(default) }
            }
        )

    val enabled = stateRepository.isEnabledFlow(serviceKey)

    fun updateInstance(default: LibRedirectDefault, instance: String): Job {
        return viewModelScope.launch(Dispatchers.IO) { defaultRepository.insert(default.copy(instanceUrl = instance)) }
    }

    fun resetServiceToFrontend(frontendState: FrontendState): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            defaultRepository.insert(frontendState.serviceKey, frontendState.frontendKey, frontendState.defaultInstance)
        }
    }

    fun updateState(enabled: Boolean): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            stateRepository.insert(LibRedirectServiceState(serviceKey, enabled))
        }
    }
}

data class BuiltInFrontendHolder(
    val key: String,
    val name: String,
    val instances: Set<String>,
    val defaultInstance: String,
)
