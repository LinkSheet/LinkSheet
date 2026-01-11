package fe.linksheet.module.resolver.urlresolver.redirect

import fe.linksheet.extension.ktor.parseHeadAsStream
import fe.linksheet.extension.ktor.refresh
import fe.linksheet.extension.ktor.urlString
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.resolver.urlresolver.base.LocalResolveRequest
import fe.linksheet.web.RefreshHeader
import fe.linksheet.web.RefreshParser
import fe.std.result.isFailure
import fe.std.result.tryCatch
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class AggressiveRedirectResolveRedirect(private val httpClient: HttpClient) : LocalResolveRequest {
    override suspend fun resolveLocal(
        url: String,
        timeout: Int
    ): Result<ResolveResultType> {
        val result = tryCatch { httpClient.get(urlString = url) }
        if (result.isFailure()) {
            return Result.failure(result.exception)
        }

        val response = result.value
        if (response.status.value in 300..399) {
            return response.toSuccessResult()
        }

        val refreshHeaderUrl = response.handleRefreshHeader()
        if (refreshHeaderUrl != null) {
            return refreshHeaderUrl.toSuccessResult()
        }

        val refreshMetaUrl = response.handleRefreshMeta()
        if (refreshMetaUrl != null) {
            return refreshMetaUrl.toSuccessResult()
        }

        return response.toSuccessResult()
    }
}

class RedirectResolveRequest(private val httpClient: HttpClient) : LocalResolveRequest {

    override suspend fun resolveLocal(url: String, timeout: Int): Result<ResolveResultType> {
        val headResult = tryCatch { httpClient.head(urlString = url) }
        if (headResult.isFailure()) {
            return Result.failure(headResult.exception)
        }

        val headResponse = headResult.value
        val refreshHeaderUrl = headResponse.handleRefreshHeader()
        if (refreshHeaderUrl != null) {
            return refreshHeaderUrl.toSuccessResult()
        }

        if (headResponse.status.value !in 400..499) {
            return headResponse.toSuccessResult()
        }

        val getResult = tryCatch { httpClient.get(urlString = url) }
        if (getResult.isFailure()) {
            return Result.failure(getResult.exception)
        }

        return getResult.value.toSuccessResult()
    }
}

private fun String.toSuccessResult(): Result<ResolveResultType> {
    return ResolveResultType.Resolved.Local(this).success()
}

private fun HttpResponse.toSuccessResult(): Result<ResolveResultType> {
    return urlString().toSuccessResult()
}

private suspend fun HttpResponse.handleRefreshMeta(): String? {
    return runCatching {
        val document = parseHeadAsStream()
        RefreshParser.parseHtml(document)?.takeIfValid()
    }.getOrNull()
}

private fun HttpResponse.handleRefreshHeader(): String? {
    return refresh()?.let { RefreshParser.parseRefreshHeader(it) }?.takeIfValid()
}

private fun RefreshHeader.takeIfValid(): String? {
    return takeIf { it.first == 0 }
        ?.takeIf { it.second.toHttpUrlOrNull() != null }
        ?.second
}
