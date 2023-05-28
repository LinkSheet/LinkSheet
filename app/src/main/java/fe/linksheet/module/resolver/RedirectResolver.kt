package fe.linksheet.module.resolver

import fe.httpkt.Request
import fe.httpkt.json.JsonBody
import fe.linksheet.module.log.HashProcessor
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.redirectResolverApiKey
import fe.linksheet.redirectResolverApiUrl
import org.koin.dsl.module
import java.net.HttpURLConnection
import java.net.URL

val redirectResolverModule = module {
    single {
        RedirectResolver(
            redirectResolverApiUrl,
            redirectResolverApiKey,
            get(),
            get<LoggerFactory>().createLogger(RedirectResolver::class)
        )
    }
}

class RedirectResolver(
    private val apiUrl: String,
    private val token: String,
    private val request: Request,
    private val logger: Logger
) {
    fun resolveRemote(url: String): HttpURLConnection {
        return request.post(apiUrl, body = JsonBody(mapOf("url" to url)), dataBuilder = {
            this.headers {
                "Authorization"("Bearer $token")
            }
        })
    }

    fun resolveLocal(url: String): HttpURLConnection {
        val con = request.head(url, followRedirects = true)
        logger.debug("ResolveLocal %s", url, HashProcessor.StringProcessor)

        return if (con.responseCode in 400..499) {
            request.getFn(url, followRedirects = true, dataBuilder = {
                headers {
                    "Host"(URL(url).host)
                }
            })
        } else con
    }
}