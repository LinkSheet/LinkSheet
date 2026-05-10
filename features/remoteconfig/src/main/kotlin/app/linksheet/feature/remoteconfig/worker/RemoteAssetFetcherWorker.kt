package app.linksheet.feature.remoteconfig.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import app.linksheet.feature.remoteconfig.preference.RemoteConfigRepository
import app.linksheet.feature.remoteconfig.preference.StoredRemotePreferences
import app.linksheet.feature.remoteconfig.service.RemoteConfigClient
import fe.std.result.isFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class RemoteAssetFetcherWorker(
    context: Context,
    parameters: WorkerParameters,
) : CoroutineWorker(context, parameters), KoinComponent {
    private val client by inject<RemoteConfigClient>()
    private val repository by inject<RemoteConfigRepository>()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val result = client.fetchLinkAssets()
        when {
            result.isFailure() -> Result.failure()
            else -> {
                repository.put(StoredRemotePreferences.linkAssets, result.value)
                Result.success()
            }
        }
    }

    companion object {
        const val TAG = "remote-assets"

        fun enqueue(context: Context) {
            WorkManager.getInstance(context).enqueue(build())
        }

        fun build(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RemoteAssetFetcherWorker>()
                .setInitialDelay(0, TimeUnit.SECONDS)
                .build()
        }

        fun buildPeriodicWorkRequest(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<RemoteAssetFetcherWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS
            ).addTag(TAG)
                .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
                .build()
        }
    }
}
