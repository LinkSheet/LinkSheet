@file:OptIn(ExperimentalTime::class)

package fe.linksheet.module.preference.state

import androidx.lifecycle.LifecycleOwner
import fe.android.lifecycle.LifecycleAwareService
import fe.android.lifecycle.koin.extension.service
import fe.linksheet.extension.koin.logger
import fe.linksheet.module.log.Logger
import fe.linksheet.module.preference.PreferenceRepositoryModule
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

val AppStateServiceModule = module {
    includes(PreferenceRepositoryModule)
    service<AppStateService> {
        AppStateService(
            clock = scope.get(),
            logger = logger,
            preferenceRepository = scope.get(),
            appStateRepository = scope.get(),
            experimentsRepository = scope.get()
        )
    }
}

internal class AppStateService(
    val clock: Clock,
    val logger: Logger,
    val preferenceRepository: AppPreferenceRepository,
    val appStateRepository: AppStateRepository,
    val experimentsRepository: ExperimentRepository,
) : LifecycleAwareService {

    private val updates = mapOf(
        AppStatePreferences.newDefaults_2024_12_16 to NewDefaults20241216,
        AppStatePreferences.newDefaults_2025_07_29 to NewDefaults20250729(preferenceRepository)
    )

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        runMigrations()
        val now = clock.now().toEpochMilliseconds()
        init(now)
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
