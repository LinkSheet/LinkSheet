package fe.linksheet.module.resolver.urlresolver

import com.google.gson.JsonParser
import fe.gson.extension.json.element.objectOrNull
import fe.gson.extension.json.`object`.asString
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequestException
import fe.std.result.isFailure
import fe.std.result.tryCatch
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*

enum class RemoteTask(val value: String) {
    All("all"),
    Redirector("redirector"),
    Amp2Html("amp2html")
}

class RemoteResolveRequest(
    apiHost: String,
    private val token: String,
    task: RemoteTask,
    private val httpClient: HttpClient,
    vararg operations: String,
) {
    private val apiUrl = "$apiHost/${task.value}"
    private val operations = operations.toList()

    suspend fun resolveRemote(url: String, timeout: Int, remoteResolveUrlField: String): Result<ResolveResultType> {
        val result = tryCatch {
            httpClient.post(urlString = apiUrl) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(RemoteResolveBody(url, operations))
            }
        }

        if (result.isFailure()) {
            return Result.failure(result.exception)
        }

        val value = result.value
        if (!value.status.isSuccess()) {
            return Result.failure(ResolveRequestException(value.status.value))
        }

        return runCatching {
            val link = value
                .bodyAsChannel()
                .toInputStream()
                .bufferedReader()
                .use { JsonParser.parseReader(it) }
                ?.objectOrNull()
                ?.asString(remoteResolveUrlField)!!
            ResolveResultType.Resolved.Remote(link)
        }
    }
}

data class RemoteResolveBody(val url: String, val operations: List<String>)
