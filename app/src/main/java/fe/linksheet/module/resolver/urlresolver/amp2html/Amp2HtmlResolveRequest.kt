package fe.linksheet.module.resolver.urlresolver.amp2html

import fe.amp2htmlkt.Amp2Html
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.resolver.urlresolver.base.LocalResolveRequest
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequestException
import fe.linksheet.web.isHtml
import fe.std.result.isFailure
import fe.std.result.tryCatch
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class Amp2HtmlResolveRequest(
    private val httpClient: HttpClient,
)  : LocalResolveRequest {

    override suspend fun resolveLocal(url: String, timeout: Int): Result<ResolveResultType> {
        val result = tryCatch {
            httpClient.get(urlString = url)
        }
        currentCoroutineContext().ensureActive()
        if (result.isFailure()) {
            return Result.failure(result.exception)
        }

        val response = result.value
        val statusCode = response.status.value

        val contentType = response.contentType() ?: return Result.failure(ResolveRequestException(statusCode))

        if (!response.status.isSuccess()) {
            return Result.failure(ResolveRequestException(statusCode))
        }

        if (!contentType.isHtml()) {
            return Result.success(ResolveResultType.NothingToResolve)
        }

        val html = response.bodyAsText()

        val nonAmpLink = parseHtml(html, response.request.url.host)
        return nonAmpLink
    }

    private fun parseHtml(html: String, host: String): Result<ResolveResultType> {
        val nonAmpLink = Amp2Html.getNonAmpLink(html, host)
        if (nonAmpLink != null) {
            return Result.success(ResolveResultType.Resolved.Local(nonAmpLink))
        }

        return Result.success(ResolveResultType.NothingToResolve)
    }
}
