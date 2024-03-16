package fe.linksheet.module.resolver.urlresolver.base

import fe.gson.extension.json.element.`object`
import fe.gson.extension.json.`object`.asString
import fe.httpkt.Request
import fe.httpkt.isHttpSuccess
import fe.httpkt.json.JsonBody
import fe.httpkt.json.readToJson
import fe.linksheet.module.log.Logger
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import java.io.IOException

class ResolveRequestException(statusCode: Int? = null) : Exception("$statusCode")

abstract class ResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
    vararg operations: String,
) : RemoteResolveRequest(apiUrl, token, request, *operations), LocalResolveRequest

interface LocalResolveRequest {
    fun resolveLocal(url: String, timeout: Int): Result<ResolveResultType>
}

abstract class RemoteResolveRequest(
    private val apiUrl: String,
    private val token: String,
    private val request: Request,
    private vararg val operations: String,
) {
    fun resolveRemote(url: String, timeout: Int, remoteResolveUrlField: String): Result<ResolveResultType> {
        val result = try {
            request.post(
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
        } catch (e: IOException) {
            return Result.failure(e)
        }

        if (!isHttpSuccess(result.responseCode)) {
            return Result.failure(ResolveRequestException(result.responseCode))
        }

        return runCatching {
            val link = result.readToJson().`object`().asString(remoteResolveUrlField)
            ResolveResultType.Resolved.Remote(link)
        }
    }
}
