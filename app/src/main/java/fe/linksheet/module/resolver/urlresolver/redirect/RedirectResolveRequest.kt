package fe.linksheet.module.resolver.urlresolver.redirect

import app.linksheet.api.CachedRequest
import fe.httpkt.Request
import fe.linksheet.extension.okhttp.parseHtmlBody
import fe.linksheet.extension.okhttp.refresh
import fe.linksheet.extension.okhttp.urlString
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequest
import fe.linksheet.web.RefreshHeader
import fe.linksheet.web.RefreshParser
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.Request as OkHttpRequest

class RedirectResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
    private val urlResolverCache: CachedRequest,
    private val okHttpClient: OkHttpClient,
    private val aggressiveExperiment: () -> Boolean = { false },
) : ResolveRequest(apiUrl, token, request, "redirect") {
    override fun resolveLocal(url: String, timeout: Int): Result<ResolveResultType> {
        if (aggressiveExperiment()) {
            return resolveAggressive(url)
        }

        val req = OkHttpRequest.Builder().url(url).head().build()

        val headResult = sendRequest(req)
        if (headResult.isFailure) {
            return Result.failure(headResult.exceptionOrNull()!!)
        }

        val headResponse = headResult.getOrNull()!!
        val refreshHeaderUrl = headResponse.handleRefreshHeader()
        if (refreshHeaderUrl != null) {
            return refreshHeaderUrl.toSuccessResult()
        }

        if (headResponse.code !in 400..499) {
            return headResponse.toSuccessResult()
        }

        val getResult = sendRequest(req.newBuilder().get().build())
        if (getResult.isFailure) {
            return Result.failure(getResult.exceptionOrNull()!!)
        }

        return getResult.getOrNull()!!.toSuccessResult()
    }

    private fun resolveAggressive(url: String): Result<ResolveResultType> {
        val req = OkHttpRequest.Builder().url(url).get().build()

        val result = sendRequest(req)
        if (result.isFailure) {
            return Result.failure(result.exceptionOrNull()!!)
        }

        val response = result.getOrNull()!!
        if (response.isRedirect) {
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

    private fun Response.handleRefreshMeta(): String? {
        return runCatching {
            RefreshParser.parseHtml(parseHtmlBody())?.takeIfValid()
        }.getOrNull()
    }

    private fun Response.handleRefreshHeader(): String? {
        return refresh()?.let { RefreshParser.parseRefreshHeader(it) }?.takeIfValid()
    }

    private fun RefreshHeader.takeIfValid(): String? {
        return takeIf { it.first == 0 }
            ?.takeIf { it.second.toHttpUrlOrNull() != null }
            ?.second
    }

    private fun String.toSuccessResult(): Result<ResolveResultType> {
        return ResolveResultType.Resolved.Local(this).success()
    }

    private fun Response.toSuccessResult(): Result<ResolveResultType> {
        return urlString().toSuccessResult()
    }

    private fun sendRequest(request: OkHttpRequest): Result<Response> {
        return runCatching { okHttpClient.newCall(request).execute() }
    }
}
