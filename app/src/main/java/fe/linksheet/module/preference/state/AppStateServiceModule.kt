package fe.linksheet.module.preference.state

import androidx.lifecycle.LifecycleOwner
import fe.android.lifecycle.LifecycleAwareService
import fe.android.lifecycle.koin.extension.service
import fe.linksheet.extension.koin.logger
import fe.linksheet.module.log.Logger
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.preferenceRepositoryModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.dsl.module

val AppStateServiceModule = module {
    includes(preferenceRepositoryModule)
    service<AppStateService> {
        AppStateService(logger, scope.get(), scope.get(), scope.get())
    }
}

internal class AppStateService(
    val logger: Logger,
    val preferenceRepository: AppPreferenceRepository,
    val appStateRepository: AppStateRepository,
    val experimentsRepository: ExperimentRepository,
) : LifecycleAwareService {

    private val updates = mapOf(
        AppStatePreferences.newDefaults_2024_12_16 to NewDefaults2024_12_16,
    )

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        runMigrations()
        init(System.currentTimeMillis())
    }

    suspend fun runMigrations() = withContext(Dispatchers.IO) {
        AppStatePreferences.runMigrations(appStateRepository)
        preferenceRepository.init()
        Experiments.runMigrations(experimentsRepository)
    }

    suspend fun init(now: Long) = withContext(Dispatchers.IO) {
        val requiredUpdates = updates.filterKeys { !appStateRepository.hasStoredValue(it) }
        logger.info("Updates required: ${requiredUpdates.size}")

        for ((preference, update) in requiredUpdates) {
            logger.info("Running update for ${preference.key}")
            update.execute(experimentsRepository)
            appStateRepository.put(preference, now)
        }
    }
}
