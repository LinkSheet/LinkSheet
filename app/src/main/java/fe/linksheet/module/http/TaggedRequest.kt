package fe.linksheet.module.http

import fe.httpkt.HttpData
import fe.httpkt.HttpData.Builder
import fe.httpkt.Request
import fe.httpkt.body.BodyData
import fe.httpkt.util.headersOf
import fe.linksheet.util.withStatsTag
import java.net.HttpURLConnection

val DefaultHeaders = headersOf(
    "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8",
    "Accept-Encoding" to "gzip, deflate",
    "Accept-Language" to "en-US,en;q=0.5",
    "Cache-Control" to "no-cache",
    "Connection" to "keep-alive",
    "Pragma" to "no-cache",
    "Sec-Fetch-Dest" to "document",
    "Sec-Fetch-Mode" to "navigate",
    "Sec-Fetch-Site" to "none",
    "Sec-Fetch-User" to "?1",
    "Upgrade-Insecure-Requests" to "1",
    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36"
)

class TaggedRequest(
    private val trafficStatsTag: Int = 0xF00D,
    data: Builder.() -> Unit
) : Request(data = data) {
    override fun get(
        url: String,
        getParams: Map<Any, Any>,
        data: HttpData?,
        connectTimeout: Int,
        readTimeout: Int,
        followRedirects: Boolean,
        maxRedirects: Int,
        forceSend: Boolean
    ): HttpURLConnection {
        return withStatsTag(trafficStatsTag) {
            super.get(url, getParams, data, connectTimeout, readTimeout, followRedirects, maxRedirects, forceSend)
        }
    }

    override fun head(
        url: String,
        getParams: Map<Any, Any>,
        data: HttpData?,
        connectTimeout: Int,
        readTimeout: Int,
        followRedirects: Boolean,
        maxRedirects: Int,
        forceSend: Boolean
    ): HttpURLConnection {
        return withStatsTag(trafficStatsTag) {
            super.head(url, getParams, data, connectTimeout, readTimeout, followRedirects, maxRedirects, forceSend)
        }
    }

    override fun request(
        method: HttpMethod,
        url: String,
        body: BodyData,
        data: HttpData?,
        connectTimeout: Int,
        readTimeout: Int,
        followRedirects: Boolean,
        maxRedirects: Int,
        forceSend: Boolean
    ): HttpURLConnection {
        return withStatsTag(trafficStatsTag) {
            super.request(
                method,
                url,
                body,
                data,
                connectTimeout,
                readTimeout,
                followRedirects,
                maxRedirects,
                forceSend
            )
        }
    }
}

