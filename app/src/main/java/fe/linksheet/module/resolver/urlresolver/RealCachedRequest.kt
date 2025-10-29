package fe.linksheet.module.resolver.urlresolver

import app.linksheet.api.CachedRequest
import fe.httpkt.Request
import fe.linksheet.module.log.Logger
import fe.linksheet.module.redactor.HashProcessor
import java.io.IOException
import java.net.HttpURLConnection

interface CachedResponse {}

data class CachedResponseImpl(
    val isSuccess: Boolean,
    val responseCode: Int,
    val contentType: String? = null,
    val content: ByteArray? = null,
) : CachedResponse

data class CacheStatus(
    val current: CachedResponseImpl?,
    val cache: CreateCache,
    val invalidateCache: () -> Unit,
    val send: () -> HttpURLConnection,
)

class RealCachedRequest(private val request: Request, private val logger: Logger) : CachedRequest {
    private val headCache = mutableMapOf<String, HttpURLConnection>()
    private val getCache = mutableMapOf<String, HttpURLConnection>()

    private val getCacheNew = mutableMapOf<String, CachedResponseImpl>()

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
    override fun head(url: String, timeout: Int, followRedirects: Boolean): HttpURLConnection {
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
    override fun get(url: String, timeout: Int, followRedirects: Boolean): HttpURLConnection {
        return getCache.getOrPut("GET", url) {
            request.get(
                url,
                connectTimeout = timeout * 1000,
                readTimeout = timeout * 1000,
                followRedirects = followRedirects
            )
        }
    }


    @Throws(IOException::class)
    fun getNew(url: String, timeout: Int, followRedirects: Boolean): CacheStatus {
        val cached = getCacheNew[url]
        val createCache: CreateCache = { getCacheNew[url] = it }
        val invalidateCache: () -> Unit = { getCacheNew.remove(url) }

        val sendRequest: () -> HttpURLConnection = {
            request.get(
                url,
                connectTimeout = timeout * 1000,
                readTimeout = timeout * 1000,
                followRedirects = followRedirects
            )
        }

        return CacheStatus(cached, createCache, invalidateCache, sendRequest)
    }

    fun clear() {
        headCache.clear()
        getCache.clear()
        getCacheNew.clear()
    }
}

typealias CreateCache = (CachedResponseImpl) -> Unit
