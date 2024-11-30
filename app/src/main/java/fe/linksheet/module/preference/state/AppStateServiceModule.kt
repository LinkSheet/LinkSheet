package fe.linksheet.module.preference.state

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LifecycleOwner
import fe.android.lifecycle.LifecycleAwareService
import fe.linksheet.extension.koin.service
import fe.linksheet.module.log.Logger
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.preferenceRepositoryModule
import org.koin.dsl.module

val AppStateServiceModule = module {
    includes(preferenceRepositoryModule)
    service<AppStateService> {
        AppStateService(logger, scope.get(), scope.get())
    }
}

class AppStateService(
    val logger: Logger,
    val appStateRepository: AppStateRepository,
    val experimentsRepository: ExperimentRepository,
) : LifecycleAwareService {

    private val updates = mapOf(
        AppStatePreferences.newDefaults_2024_11_29 to { experimentsRepository.put(Experiments.loopDetector, true) },
        AppStatePreferences.newDefaults_2024_11_30 to { experimentsRepository.put(Experiments.editClipboard, true) }
    )

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        init(System.currentTimeMillis())
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun init(now: Long) {
        val requiredUpdates = updates.filterKeys { !appStateRepository.hasStoredValue(it) }
        logger.info("Updates required: ${requiredUpdates.size}")

        for ((preference, update) in requiredUpdates) {
            logger.info("Running update for ${preference.key}")
            update()
            appStateRepository.put(preference, now)
        }
    }
}
