package fe.linksheet.module.resolver.urlresolver.redirect

import fe.httpkt.Request
import fe.linksheet.util.buildconfig.LinkSheetAppConfig
import fe.linksheet.extension.koin.single
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequest
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Response
import org.koin.dsl.module

val redirectResolveRequestModule = module {
    single<RedirectResolveRequest, Request, CachedRequest> { _, request, cachedRequest ->
        RedirectResolveRequest(
            "${LinkSheetAppConfig.supabaseHost()}/redirector",
            LinkSheetAppConfig.supabaseApiKey(),
            request, cachedRequest, scope.get<OkHttpClient>()
        )
    }
}

class RedirectResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
    private val urlResolverCache: CachedRequest,
    private val okHttpClient: OkHttpClient,
) : ResolveRequest(apiUrl, token, request, "redirect") {
    override fun resolveLocal(url: String, timeout: Int): Result<ResolveResultType> {
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

    private fun Response.handleRefreshHeader(): String? {
        val refreshHeader = header("refresh") ?: return null
        val parsedHeader = parseRefreshHeader(refreshHeader) ?: return null

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

    companion object {
        private val refreshHeaderRegex = Regex("(\\d+)(?:\\.\\d*)?[;,](?:URL=)?(.+)", RegexOption.IGNORE_CASE)

        internal fun parseRefreshHeader(refreshHeader: String): Pair<Int, String>? {
            fun unquoteHeader(value: String): String {
                if (value.length <= 2) return value

                val firstChar = value[0]
                val lastChar = value[value.length - 1]

                if ((firstChar == '"' && lastChar == '"') || (firstChar == '\'' && lastChar == '\'')) {
                    return value.substring(1, value.length - 1)
                }

                return value
            }

            val (_, time, url) = refreshHeaderRegex.matchEntire(refreshHeader)?.groupValues ?: return null
            val intTime = time.toIntOrNull() ?: return null

            return intTime to unquoteHeader(url)
        }
    }
}
