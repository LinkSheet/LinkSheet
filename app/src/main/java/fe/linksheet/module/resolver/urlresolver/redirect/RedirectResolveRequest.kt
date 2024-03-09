package fe.linksheet.module.resolver.urlresolver.redirect

import fe.httpkt.Request
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.extension.koin.single
import fe.linksheet.module.log.Logger
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequest
import org.koin.dsl.module

val redirectResolveRequestModule = module {
    single<RedirectResolveRequest, Request, CachedRequest> { _, request, cachedRequest ->
        RedirectResolveRequest(
            "${LinkSheetAppConfig.supabaseHost()}/redirector",
            LinkSheetAppConfig.supabaseApiKey(),
            request, cachedRequest
        )
    }
}

class RedirectResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
    private val urlResolverCache: CachedRequest,
) : ResolveRequest(apiUrl, token, request, "redirect") {
    override fun resolveLocal(url: String, timeout: Int): Result<ResolveResultType> {
        val result = try {
            urlResolverCache.head(url, timeout, true)
        } catch (e: Exception) {
            return Result.failure(e)
        }

        val response = if (result.responseCode in 400..499) {
            urlResolverCache.get(url, timeout, true)
        } else result

        return ResolveResultType.Resolved.Local(response.url.toString()).success()
    }
}
