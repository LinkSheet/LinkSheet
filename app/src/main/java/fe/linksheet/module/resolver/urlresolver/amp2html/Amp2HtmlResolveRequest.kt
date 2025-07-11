package fe.linksheet.module.resolver.urlresolver.amp2html

import fe.amp2htmlkt.Amp2Html
import fe.droidkit.koin.single
import fe.httpkt.Request
import fe.linksheet.extension.okhttp.isHtml
import fe.linksheet.module.repository.CacheRepository
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequest
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequestException
import fe.linksheet.util.buildconfig.LinkSheetAppConfig
import okhttp3.OkHttpClient
import okhttp3.Response
import org.koin.dsl.module

val amp2HtmlResolveRequestModule = module {
    single<Amp2HtmlResolveRequest, Request, CachedRequest> { _, request, cachedRequest ->
        Amp2HtmlResolveRequest(
            "${LinkSheetAppConfig.supabaseHost()}/amp2html",
            LinkSheetAppConfig.supabaseApiKey(),
            request,
            scope.get<CacheRepository>(),
            cachedRequest,
            scope.get<OkHttpClient>()
        )
    }
}

class Amp2HtmlResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
    private val cacheRepository: CacheRepository,
    private val urlResolverCache: CachedRequest,
    private val okHttpClient: OkHttpClient,
) : ResolveRequest(apiUrl, token, request, "amp2html") {
    override fun resolveLocal(url: String, timeout: Int): Result<ResolveResultType> {
        val req = okhttp3.Request.Builder().url(url).build()

        val result = sendRequest(req)
        if (result.isFailure) {
            return Result.failure(result.exceptionOrNull()!!)
        }

        val response = result.getOrNull()!!
        val statusCode = response.code
        response.body.use {
            val contentType = it.contentType() ?: return Result.failure(ResolveRequestException(statusCode))

            if (!response.isSuccessful) {
                return Result.failure(ResolveRequestException(statusCode))
            }

            if (!contentType.isHtml()) {
                return Result.success(ResolveResultType.NothingToResolve)
            }

            val html = it.string()

            val nonAmpLink = parseHtml(html, req.url.host)
            if (nonAmpLink != null) {
                return nonAmpLink
            }

            return Result.failure(ResolveRequestException())
        }
    }

    private fun parseHtml(html: String, host: String): Result<ResolveResultType>? {
        val nonAmpLink = Amp2Html.getNonAmpLink(html, host)
        if (nonAmpLink != null) {
            return Result.success(ResolveResultType.Resolved.Local(nonAmpLink))
        }

        return Result.success(ResolveResultType.NothingToResolve)
    }

    private fun sendRequest(request: okhttp3.Request): Result<Response> {
        return runCatching { okHttpClient.newCall(request).execute() }
    }
}
