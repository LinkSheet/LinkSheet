package fe.linksheet.module.resolver.urlresolver.redirect

import app.linksheet.api.CachedRequest
import fe.httpkt.Request
import fe.linksheet.RedirectResolver
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequest
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Response

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

        val req = okhttp3.Request.Builder().url(url).head().build()

        val headResult = sendRequest(req)
        if (headResult.isFailure) {
            return Result.failure(headResult.exceptionOrNull()!!)
        }

        val headResponse = headResult.getOrNull()!!
        val refreshHeaderUrl = headResponse.handleRefreshHeader()
        if (refreshHeaderUrl != null) {
            return ResolveResultType.Resolved.Local(refreshHeaderUrl).success()
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
        val req = okhttp3.Request.Builder().url(url).get().build()

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
            return ResolveResultType.Resolved.Local(refreshHeaderUrl).success()
        }

        return response.toSuccessResult()
    }

    private fun Response.handleRefreshHeader(): String? {
        val refreshHeader = header("refresh") ?: return null
        val parsedHeader = RedirectResolver.parseRefreshHeader(refreshHeader) ?: return null

        return parsedHeader
            .takeIf { it.first == 0 }
            ?.takeIf { it.second.toHttpUrlOrNull() != null }
            ?.second
    }

    private fun Response.toSuccessResult(): Result<ResolveResultType> {
        return ResolveResultType.Resolved.Local(request.url.toString()).success()
    }

    private fun sendRequest(request: okhttp3.Request): Result<Response> {
        return runCatching { okHttpClient.newCall(request).execute() }
    }
}
