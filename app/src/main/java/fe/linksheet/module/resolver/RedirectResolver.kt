package fe.linksheet.module.resolver

import fe.httpkt.HttpData
import fe.httpkt.Request
import fe.httpkt.json.JsonBody
import fe.linksheet.module.log.DebugLogger
import fe.linksheet.module.log.HashProcessor
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.request.requestModule
import fe.linksheet.redirectResolverApiKey
import fe.linksheet.redirectResolverApiUrl
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.io.IOException
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
    @Throws(IOException::class)
    fun resolveRemote(url: String, connectTimeout: Int): HttpURLConnection {
        return request.post(
            apiUrl,
            connectTimeout = connectTimeout * 1000,
            body = JsonBody(mapOf("url" to url)),
            dataBuilder = {
                this.headers {
                    "Authorization"("Bearer $token")
                }
            }
        )
    }

    @Throws(IOException::class)
    fun resolveLocal(url: String, connectTimeout: Int): HttpURLConnection {
        val con = request.head(url, connectTimeout = connectTimeout * 1000, followRedirects = true)
        logger.debug("ResolveLocal %s", url, HashProcessor.StringProcessor)

        return if (con.responseCode in 400..499) {
            request.get(
                url,
                connectTimeout = connectTimeout * 1000,
                followRedirects = true,
                data = HttpData.of {
                    headers {
                        "Host"(URL(url).host)
                    }
                }
            )
        } else con
    }
}