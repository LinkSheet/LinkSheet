package fe.linksheet.module.resolver.urlresolver.redirect

import fe.httpkt.Request
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.module.log.HashProcessor
import fe.linksheet.module.log.Logger
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequest
import fe.linksheet.supabaseApiKey
import fe.linksheet.supabaseFunctionHost
import org.koin.dsl.module
import java.io.IOException

val redirectResolveRequestModule = module {
    single {
        RedirectResolveRequest(
            "$supabaseFunctionHost/redirector",
            supabaseApiKey,
            get(),
            get(),
            createLogger<RedirectUrlResolver>()
        )
    }
}

class RedirectResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
    private val urlResolverCache: CachedRequest,
    logger: Logger
) : ResolveRequest(apiUrl, token, request, logger, "redirect") {
    @Throws(IOException::class)
    override fun resolveLocal(url: String, timeout: Int): String {
        val con = urlResolverCache.head(url, timeout, true)
        logger.debug({"ResolveLocal $it"}, url, HashProcessor.StringProcessor)

        val response = if (con.responseCode in 400..499) {
            urlResolverCache.get(url, timeout, true)
        } else con

        return response.url.toString()
    }
}