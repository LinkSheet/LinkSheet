package app.linksheet.feature.libredirect.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.linksheet.feature.libredirect.FrontendState
import app.linksheet.feature.libredirect.ServiceSettings
import app.linksheet.feature.libredirect.SettingsController
import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import app.linksheet.feature.libredirect.database.entity.LibRedirectServiceState
import app.linksheet.feature.libredirect.database.entity.LibRedirectUserInstance
import app.linksheet.feature.libredirect.database.repository.LibRedirectDefaultRepository
import app.linksheet.feature.libredirect.database.repository.LibRedirectStateRepository
import app.linksheet.feature.libredirect.database.repository.LibRedirectUserInstanceRepository
import fe.linksheet.extension.kotlin.ProduceSideEffect
import fe.linksheet.extension.kotlin.mapProducingSideEffect
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LibRedirectServiceSettingsViewModel(
    val context: Application,
    private val serviceKey: String,
    private val defaultRepository: LibRedirectDefaultRepository,
    private val stateRepository: LibRedirectStateRepository,
    private val userInstanceRepository: LibRedirectUserInstanceRepository,
    val customInstancesExperiment: () -> Boolean,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
//    val enableLibRedirect = preferenceRepository.asViewModelState(libRedirectPreferences.enable)
    private val controller = SettingsController()

    private val _settings = MutableStateFlow<ServiceSettings?>(null)
    val settings = _settings.asStateFlow()

    fun loadSettings() {
        viewModelScope.launch {
            _settings.value = controller.loadSettings(serviceKey)
        }
    }

    fun getUserInstances(frontend: String): Flow<List<String>> {
        return userInstanceRepository.getByServiceAndFrontendOrNull(serviceKey, frontend).map { list ->
            list.map { it.instanceUrl }
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
//        userInstanceRepository.getByServiceAndFrontendOrNull(serviceKey)

        if (stored == null) {
            return settings.fallback!! to settings.defaultFrontend!!
        }

        val frontend = settings.maybeGetFrontend(stored.frontendKey)
        if (frontend == null) {
            handleSideEffect(stored)
            return settings.fallback!! to settings.defaultFrontend!!
        }

        // Url is no longer available in instances shipped
        if (stored.instanceUrl != LibRedirectDefault.randomInstance && stored.instanceUrl !in frontend.instances) {
            // TODO: Use version to do a more exhaustive check? Otherwise assume instance is gone, fall back to new default
            if (!stored.userDefined) {
                return settings.fallback!! to settings.defaultFrontend!!
//                     TODO: Do something about this
//                    return@mapProducingSideEffect stored.copy(instanceUrl = fallback.instanceUrl)
            }
        }

        return stored to frontend
    }


    val selectedFrontend: Flow<Pair<LibRedirectDefault, FrontendState>> = _settings
        .filterNotNull()
        .combine(defaultRepository.getByServiceKey(serviceKey)) { settings, stored ->
            settings to stored
        }
        .mapProducingSideEffect(
            sideEffectContext = ioDispatcher,
            transform = ::transform,
            handleSideEffect = { default -> defaultRepository.delete(default) }
        )

    val enabled = stateRepository.isEnabledFlow(serviceKey)

    fun updateInstance(
        default: LibRedirectDefault,
        instance: String,
        userDefined: Boolean
    ) = viewModelScope.launch(ioDispatcher) {
        defaultRepository.insert(
            default.copy(
                instanceUrl = instance,
                version = _settings.value?.getCurrentVersion() ?: 0,
                userDefined = userDefined
            )
        )
    }

    fun resetServiceToFrontend(frontendState: FrontendState): Job {
        return viewModelScope.launch(ioDispatcher) {
            defaultRepository.insert(frontendState.serviceKey, frontendState.frontendKey, frontendState.defaultInstance)
        }
    }

    fun updateState(enabled: Boolean): Job {
        return viewModelScope.launch(ioDispatcher) {
            stateRepository.insert(LibRedirectServiceState(serviceKey, enabled))
        }
    }

    fun addInstance(frontend: String, instance: String) {
        viewModelScope.launch(ioDispatcher) {
            userInstanceRepository.insert(LibRedirectUserInstance(serviceKey, frontend, instance))
        }
    }

    fun deleteInstance(frontend: String, instance: String) {
        viewModelScope.launch(ioDispatcher) {
            userInstanceRepository.delete(LibRedirectUserInstance(serviceKey, frontend, instance))
        }
    }
}

data class BuiltInFrontendHolder(
    val key: String,
    val name: String,
    val instances: Set<String>,
    val defaultInstance: String,
)
