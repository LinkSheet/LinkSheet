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

enum class RemoteTask(val value: String, vararg operations: String) {
    All("all", "redirect", "amp2html"),
    Redirector("redirector", "redirect"),
    Amp2Html("amp2html", "amp2html");

    val operations = operations.toList()
}

data class RemoteResolveBody(val url: String, val operations: List<String>)

class RemoteResolver(
    private val apiHost: String,
    private val token: String,
    private val httpClient: HttpClient
) {
    suspend fun resolveRemote(task: RemoteTask, url: String, timeout: Int, remoteResolveUrlField: String): Result<ResolveResultType> {
        val apiUrl = "$apiHost/${task.value}"
        val result = tryCatch {
            httpClient.post(urlString = apiUrl) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(RemoteResolveBody(url, task.operations))
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
