package fe.linksheet.module.resolver.urlresolver

import fe.httpkt.Request
import fe.linksheet.module.log.HashProcessor
import fe.linksheet.module.log.LoggerFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.net.HttpURLConnection

val cachedRequestModule = module {
    singleOf(::CachedRequest)
}

class CachedRequest(private val request: Request, loggerFactory: LoggerFactory) {
    private val logger = loggerFactory.createLogger(CachedRequest::class)

    private val headCache = mutableMapOf<String, HttpURLConnection>()
    private val getCache = mutableMapOf<String, HttpURLConnection>()

    private inline fun MutableMap<String, HttpURLConnection>.getOrPut(
        type: String,
        url: String,
        defaultValue: () -> HttpURLConnection
    ): HttpURLConnection {
        val value = get(url)
        return if (value == null) {
            logger.debug(
                { "No cached response found for $type $it, sending request.." },
                url,
                HashProcessor.UrlProcessor
            )
            val answer = defaultValue()
            put(url, answer)
            answer
        } else {
            logger.debug({ "Cached response found for $type $it!" }, url,
                HashProcessor.UrlProcessor
            )
            value
        }
    }

    fun head(
        url: String,
        timeout: Int,
        followRedirects: Boolean
    ) = headCache.getOrPut("HEAD", url) {
        request.head(
            url,
            connectTimeout = timeout * 1000,
            readTimeout = timeout * 1000,
            followRedirects = followRedirects
        )
    }

    fun clear() {
        headCache.clear()
        getCache.clear()
    }

    fun get(
        url: String,
        timeout: Int,
        followRedirects: Boolean
    ) = getCache.getOrPut("GET", url) {
        request.get(
            url,
            connectTimeout = timeout * 1000,
            readTimeout = timeout * 1000,
            followRedirects = followRedirects
        )
    }
}