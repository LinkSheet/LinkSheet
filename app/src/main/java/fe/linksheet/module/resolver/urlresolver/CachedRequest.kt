package fe.linksheet.module.resolver.urlresolver

import fe.httpkt.Request
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.module.redactor.HashProcessor
import org.koin.core.component.KoinComponent
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.io.IOException
import java.net.HttpURLConnection

val cachedRequestModule = module {
    singleOf(::CachedRequest)
}

class CachedRequest(private val request: Request) : KoinComponent {
    private val logger by injectLogger<CachedRequest>()

    private val headCache = mutableMapOf<String, HttpURLConnection>()
    private val getCache = mutableMapOf<String, HttpURLConnection>()

    @Throws(IOException::class)
    private inline fun MutableMap<String, HttpURLConnection>.getOrPut(
        type: String,
        url: String,
        defaultValue: () -> HttpURLConnection,
    ): HttpURLConnection {
        val value = get(url)
        return if (value == null) {
            logger.debug(
                url,
                HashProcessor.UrlProcessor
            ) { "No cached response found for $type $it, sending request.." }
            val answer = defaultValue()
            put(url, answer)
            answer
        } else {
            logger.debug(url, HashProcessor.UrlProcessor) { "Cached response found for $type $it!" }
            value
        }
    }

    @Throws(IOException::class)
    fun head(url: String, timeout: Int, followRedirects: Boolean): HttpURLConnection {
        return headCache.getOrPut("HEAD", url) {
            request.head(
                url,
                connectTimeout = timeout * 1000,
                readTimeout = timeout * 1000,
                followRedirects = followRedirects
            )
        }
    }

    @Throws(IOException::class)
    fun get(url: String, timeout: Int, followRedirects: Boolean): HttpURLConnection {
        return getCache.getOrPut("GET", url) {
            request.get(
                url,
                connectTimeout = timeout * 1000,
                readTimeout = timeout * 1000,
                followRedirects = followRedirects
            )
        }
    }

    fun clear() {
        headCache.clear()
        getCache.clear()
    }
}
