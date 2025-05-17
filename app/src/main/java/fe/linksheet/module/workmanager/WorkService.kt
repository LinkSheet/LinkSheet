package fe.linksheet.module.workmanager

import androidx.lifecycle.LifecycleOwner
import androidx.work.*
import fe.android.lifecycle.LifecycleAwareService
import fe.android.lifecycle.koin.extension.service
import fe.linksheet.module.remoteconfig.RemoteAssetFetcherWorker
import fe.linksheet.module.remoteconfig.RemoteConfigClientModule
import org.koin.dsl.module

val workServiceModule = module {
    includes(RemoteConfigClientModule)
    service<WorkService> {
        WorkService(WorkManager.getInstance(applicationContext))
    }
}

class WorkService(val workManager: WorkManager) : LifecycleAwareService {

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        workManager.enqueueUniquePeriodicWork(
            RemoteAssetFetcherWorker.tag,
            ExistingPeriodicWorkPolicy.KEEP,
            RemoteAssetFetcherWorker.buildPeriodicWorkRequest()
        )
    }
}
