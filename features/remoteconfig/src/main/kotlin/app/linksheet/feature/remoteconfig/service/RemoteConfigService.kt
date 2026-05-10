package app.linksheet.feature.remoteconfig.service

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import app.linksheet.feature.remoteconfig.worker.RemoteAssetFetcherWorker
import app.linksheet.log.createLogger
import fe.android.lifecycle.LifecycleAwareService
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RemoteConfigService(
    private val workManager: WorkManager,
    private val enabled: StateFlow<Boolean>
) : LifecycleAwareService {

    private val logger = createLogger<RemoteConfigService>()

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        owner.lifecycleScope.launch {
            enabled.collect { setRemoteConfig(it) }
        }
    }

    private fun setRemoteConfig(enabled: Boolean) {
        logger.debug("Updating remote config work state: $enabled")
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
