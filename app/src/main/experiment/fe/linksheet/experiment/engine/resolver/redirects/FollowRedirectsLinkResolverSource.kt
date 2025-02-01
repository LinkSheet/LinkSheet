package fe.linksheet.experiment.engine.resolver.redirects

import fe.linksheet.module.resolver.urlresolver.redirect.RedirectResolveRequest.Companion.parseRefreshHeader
import fe.std.result.*
import io.ktor.client.*
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import me.saket.unfurl.extension.HtmlMetadataUnfurlerExtension


class FollowRedirectsLocalSource(engine: HttpClientEngine) : FollowRedirectsSource {
    private val client = HttpClient(engine) {
        defaultRequest { configureHeaders() }
    }

    override suspend fun resolve(urlString: String): IResult<FollowRedirectsResult> {
        val headResult = tryCatch { client.head(urlString = urlString) }
        if (headResult.isFailure()) {
            return +headResult
        }

        val headResponse = headResult.value
        val headRefreshHeaderUrl = headResponse.handleRefreshHeader()
        if (headRefreshHeaderUrl != null) {
            return FollowRedirectsResult.RefreshHeader(headRefreshHeaderUrl).success
        }

        if (headResponse.status.value !in 400..499) {
            return FollowRedirectsResult.LocationHeader(headResponse.request.url.toString()).success
        }

        val getResult = tryCatch { client.get(urlString = urlString) }
        if (getResult.isFailure()) {
            return +getResult
        }

        val getResponse = getResult.value
        val getRefreshHeaderUrl = getResponse.handleRefreshHeader()
        if (getRefreshHeaderUrl != null) {
            return FollowRedirectsResult.RefreshHeader(getRefreshHeaderUrl).success
        }

        return FollowRedirectsResult.GetRequest(getResponse.request.url.toString(), getResponse.bodyAsText()).success
    }

    private fun HttpMessageBuilder.configureHeaders(
        httpUserAgent: String = HtmlMetadataUnfurlerExtension.SlackBotUserAgent,
        htmlByteLimit: Long = 32_768
    ) {
        header("User-Agent", httpUserAgent)
        header("Accept", "text/html")
        header("Accept-Language", "en-US,en;q=0.5")
        header("Range", "bytes=0-$htmlByteLimit")
    }

    private fun HttpResponse.handleRefreshHeader(): String? {
        val refreshHeader = headers["refresh"] ?: return null
        val parsedHeader = parseRefreshHeader(refreshHeader) ?: return null

        return parsedHeader
            .takeIf { it.first == 0 }
            ?.takeIf { parseUrl(it.second) != null }
            ?.second
    }
}

sealed class FollowRedirectsResult(val url: String) {
    class RefreshHeader(url: String) : FollowRedirectsResult(url)
    class LocationHeader(url: String) : FollowRedirectsResult(url)
    class GetRequest(url: String, val body: String) : FollowRedirectsResult(url)
}
