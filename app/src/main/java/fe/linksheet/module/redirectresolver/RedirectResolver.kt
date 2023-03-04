package fe.linksheet.module.redirectresolver

import android.util.Log
import fe.httpkt.Request
import fe.httpkt.json.JsonBody
import fe.linksheet.redirectResolverApiKey
import fe.linksheet.redirectResolverApiUrl
import org.koin.dsl.module
import java.net.HttpURLConnection
import java.net.URL

val redirectResolverModule = module {
    single {
        RedirectResolver(redirectResolverApiUrl, redirectResolverApiKey, get())
    }
}

class RedirectResolver(
    private val apiUrl: String,
    private val token: String,
    private val request: Request
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
        Log.d("ResolveLocal", "$con")
        return if (con.responseCode in 400..499) {
            request.getFn(url, followRedirects = true, dataBuilder = {
                headers {
                    "Host"(URL(url).host)
                    "User-Agent"("Mozilla/5.0 (Windows NT 10.0; rv:110.0) Gecko/20100101 Firefox/110.0")
                    "Accept"("text/html)application/xhtml+xml)application/xml;q=0.9)image/avif)image/webp)*/*;q=0.8")
                    "Accept-Language"("en-US)en;q=0.5")
                    "Accept-Encoding"("gzip) deflate) br")
                    "Connection"("keep-alive")
                    "Upgrade-Insecure-Requests"("1")
                    "Sec-Fetch-Dest"("document")
                    "Sec-Fetch-Mode"("navigate")
                    "Sec-Fetch-Site"("none")
                    "Sec-Fetch-User"("?1")
                    "Pragma"("no-cache")
                    "Cache-Control"("no-cache")
                }
            })
        } else con
    }
}