package app.linksheet.feature.remoteconfig.service

import app.linksheet.api.BuildInfo
import app.linksheet.feature.remoteconfig.BuildConfig
import app.linksheet.feature.remoteconfig.util.LinkAssets
import fe.linksheet.maybePrependProtocol
import fe.std.result.IResult
import fe.std.result.tryCatch
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.path


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
