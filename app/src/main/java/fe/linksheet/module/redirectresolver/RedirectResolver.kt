package fe.linksheet.module.redirectresolver

import fe.httpkt.Request
import fe.httpkt.json.JsonBody
import fe.linksheet.redirectResolverApiKey
import fe.linksheet.redirectResolverApiUrl
import org.koin.dsl.module
import timber.log.Timber
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
        Timber.tag("ResolveLocal").d("$con")
        return if (con.responseCode in 400..499) {
            request.getFn(url, followRedirects = true, dataBuilder = {
                headers {
                    "Host"(URL(url).host)
                }
            })
        } else con
    }
}