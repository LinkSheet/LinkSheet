package fe.linksheet.module.remoteconfig

import android.content.Context
import androidx.work.*
import fe.linksheet.BuildConfig
import fe.linksheet.module.http.HttpModule
import fe.linksheet.util.LinkAssets
import fe.linksheet.util.buildconfig.LinkSheetInfo
import fe.linksheet.util.maybePrependProtocol
import fe.std.result.isFailure
import fe.std.result.tryCatch
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.util.concurrent.TimeUnit


val RemoteConfigClientModule = module {
    includes(HttpModule)
    singleOf(::RemoteConfigRepository)
    single { AndroidRemoteConfigClient(get()) }
    workerOf(::RemoteAssetFetcherWorker)
}

@Suppress("FunctionName")
internal fun AndroidRemoteConfigClient(client: HttpClient): RemoteConfigClient {
    val version = LinkSheetInfo.buildInfo.versionName
    val userAgent = "LinkSheet/$version"
    return RemoteConfigClient(
        apiHost = BuildConfig.API_HOST.maybePrependProtocol("https"),
        userAgent = userAgent,
        client = client
    )
}

class RemoteConfigClient(
    private val apiHost: String,
    private val userAgent: String,
    private val client: HttpClient
) {
    suspend fun fetchLinkAssets(): LinkAssets {
        val response = client.get(urlString = apiHost) {
            url { path("assets") }
            headers { append(HttpHeaders.UserAgent, userAgent) }
        }

        return response.body<LinkAssets>()
    }
}

class RemoteAssetFetcherWorker(
    private val client: RemoteConfigClient,
    private val repository: RemoteConfigRepository,
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val result = tryCatch { client.fetchLinkAssets() }
        when {
            result.isFailure() -> Result.failure()
            else -> {
                repository.put(RemoteConfigPreferences.linkAssets, result.value)
                Result.success()
            }
        }
    }

    companion object {
        fun enqueue(context: Context) {
            WorkManager.getInstance(context).enqueue(build())
        }

        fun build(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RemoteAssetFetcherWorker>()
                .setInitialDelay(0, TimeUnit.SECONDS)
                .build()
        }
    }
}
