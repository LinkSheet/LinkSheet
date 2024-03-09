package fe.linksheet.module.resolver.urlresolver.base

import fe.httpkt.Request
import fe.httpkt.json.JsonBody
import fe.linksheet.module.log.Logger
import java.io.IOException
import java.net.HttpURLConnection


abstract class ResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
    logger: Logger,
    vararg operations: String,
) : RemoteResolveRequest(apiUrl, token, request, logger, *operations), LocalResolveRequest

interface LocalResolveRequest {
    @Throws(IOException::class)
    fun resolveLocal(url: String, timeout: Int): String?
}

abstract class RemoteResolveRequest(
    private val apiUrl: String,
    private val token: String,
    private val request: Request,
    protected val logger: Logger,
    private vararg val operations: String,
) {
    @Throws(IOException::class)
    fun resolveRemote(url: String, timeout: Int): HttpURLConnection {
        return request.post(
            apiUrl,
            connectTimeout = timeout * 1000,
            readTimeout = timeout * 1000,
            body = JsonBody(mapOf("url" to url, "operations" to operations.toList())),
            dataBuilder = {
                this.headers {
                    "Authorization"("Bearer $token")
                }
            }
        )
    }
}
