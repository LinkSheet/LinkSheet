package fe.linksheet.module.resolver.urlresolver.redirect

import fe.httpkt.Request
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.extension.koin.single
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequest
import okhttp3.OkHttpClient
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
        val response = okHttpClient.newCall(req).execute()
        val statusCode = response.code


//        val result = try {
//            urlResolverCache.head(url, timeout, true)
//        } catch (e: Exception) {
//            return Result.failure(e)
//        }
//
        val req2 = if (statusCode in 400..499) {
            req.newBuilder().get().build()
        } else req

        return ResolveResultType.Resolved.Local(req2.url.toString()).success()
    }
}
