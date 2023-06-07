package fe.linksheet.module.resolver.urlresolver.base

import fe.httpkt.Request
import fe.httpkt.json.JsonBody
import fe.linksheet.module.log.Logger
import java.io.IOException
import java.net.HttpURLConnection


abstract class ResolveRequest(
    private val apiUrl: String,
    private val token: String,
    protected val request: Request,
    protected val logger: Logger
) {
    @Throws(IOException::class)
    fun resolveRemote(url: String, timeout: Int): HttpURLConnection {
        return request.post(
            apiUrl,
            connectTimeout = timeout * 1000,
            readTimeout = timeout * 1000,
            body = JsonBody(mapOf("url" to url)),
            dataBuilder = {
                this.headers {
                    "Authorization"("Bearer $token")
                }
            }
        )
    }

    @Throws(IOException::class)
    abstract fun resolveLocal(url: String, timeout: Int): String?
}