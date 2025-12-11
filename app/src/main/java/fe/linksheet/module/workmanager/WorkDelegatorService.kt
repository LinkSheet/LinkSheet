package fe.linksheet.module.workmanager

import androidx.lifecycle.LifecycleOwner
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import fe.android.lifecycle.LifecycleAwareService
import fe.android.lifecycle.koin.extension.service
import fe.composekit.preference.asFunction
import fe.linksheet.module.preference.PreferenceRepositoryModule
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.remoteconfig.RemoteAssetFetcherWorker
import fe.linksheet.module.remoteconfig.RemoteConfigClientModule
import org.koin.dsl.module

val WorkDelegatorServiceModule = module {
    includes(PreferenceRepositoryModule, RemoteConfigClientModule)
    service<WorkDelegatorService> {
        WorkDelegatorService(
            workManager = WorkManager.getInstance(applicationContext),
            remoteConfig = scope.get<AppPreferenceRepository>().asFunction(AppPreferences.remoteConfig)
        )
    }
}

class WorkDelegatorService(
    val workManager: WorkManager,
    val remoteConfig: () -> Boolean,
) : LifecycleAwareService {

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        setRemoteConfig(remoteConfig())
    }

    fun setRemoteConfig(enabled: Boolean) {
        if (enabled) {
            workManager.enqueueUniquePeriodicWork(
                RemoteAssetFetcherWorker.TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                RemoteAssetFetcherWorker.buildPeriodicWorkRequest()
            )
        } else {
            workManager.cancelAllWorkByTag(RemoteAssetFetcherWorker.TAG)
        }
    }
}
