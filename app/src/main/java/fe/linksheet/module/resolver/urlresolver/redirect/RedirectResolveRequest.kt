package fe.linksheet.module.resolver.urlresolver.redirect

import fe.httpkt.HttpData
import fe.httpkt.Request
import fe.linksheet.extension.createLogger
import fe.linksheet.module.log.HashProcessor
import fe.linksheet.module.log.Logger
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequest
import fe.linksheet.supabaseApiKey
import fe.linksheet.supabaseFunctionHost
import org.koin.dsl.module
import java.io.IOException
import java.net.URL

val redirectResolveRequestModule = module {
    single {
        RedirectResolveRequest(
            "$supabaseFunctionHost/redirector",
            supabaseApiKey,
            get(),
            createLogger<RedirectUrlResolver>()
        )
    }
}

class RedirectResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
    logger: Logger
) : ResolveRequest(apiUrl, token, request, logger) {
    @Throws(IOException::class)
    override fun resolveLocal(url: String, timeout: Int): String {
        val con = request.head(
            url,
            connectTimeout = timeout * 1000,
            readTimeout = timeout * 1000,
            followRedirects = true
        )
        logger.debug("ResolveLocal %s", url, HashProcessor.StringProcessor)

        val response = if (con.responseCode in 400..499) {
            request.get(
                url,
                connectTimeout = timeout * 1000,
                readTimeout = timeout * 1000,
                followRedirects = true,
                data = HttpData.of {
                    headers {
                        "Host"(URL(url).host)
                    }
                }
            )
        } else con

        return response.url.toString()
    }
}