package fe.linksheet.module.remoteconfig

import android.content.Context
import androidx.work.*
import fe.droidkit.koin.getResources
import fe.linksheet.BuildConfig
import fe.linksheet.R.string
import fe.linksheet.module.http.HttpModule
import fe.linksheet.module.systeminfo.BuildInfo
import fe.linksheet.module.systeminfo.SystemInfoService
import fe.linksheet.module.systeminfo.SystemInfoServiceModule
import fe.linksheet.util.LinkAssets
import fe.linksheet.util.maybePrependProtocol
import fe.std.result.IResult
import fe.std.result.isFailure
import fe.std.result.tryCatch
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.util.concurrent.TimeUnit


val RemoteConfigClientModule = module {
    includes(HttpModule, SystemInfoServiceModule)
    singleOf(::RemoteConfigRepository)
    single {
        AndroidRemoteConfigClient(
            appName = getResources().getString(string.app_name),
            buildInfo = get<SystemInfoService>().buildInfo,
            client = get(),
        )
    }
    workerOf(::RemoteAssetFetcherWorker)
}

@Suppress("FunctionName")
internal fun AndroidRemoteConfigClient(appName: String, buildInfo: BuildInfo, client: HttpClient): RemoteConfigClient {
    val version = buildInfo.versionName
    return RemoteConfigClient(
        apiHost = BuildConfig.API_HOST.maybePrependProtocol("https"),
        userAgent = "$appName/$version",
        client = client
    )
}

class RemoteConfigClient(
    private val apiHost: String,
    private val userAgent: String,
    private val client: HttpClient,
) {
    suspend fun fetchLinkAssets(): IResult<LinkAssets> {
        val response = client.get(urlString = apiHost) {
            url { path("assets") }
            headers { append(HttpHeaders.UserAgent, userAgent) }
        }

        return tryCatch { response.body<LinkAssets>() }
    }
}

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
                repository.put(RemoteConfigPreferences.linkAssets, result.value)
                Result.success()
            }
        }
    }

    companion object {
        const val tag = "remote-assets"

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
            ).addTag(tag)
                .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
                .build()
        }
    }
}
