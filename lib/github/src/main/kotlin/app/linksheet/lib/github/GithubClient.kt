package app.linksheet.lib.github

import app.linksheet.lib.github.model.Release
import fe.std.result.StdResult
import fe.std.result.isFailure
import fe.std.result.tryCatch
import fe.std.result.unaryPlus
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GithubClient(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun fetchText(url: String, skipCache: Boolean): StdResult<String> {
        val result = tryCatch {
            client.get(urlString = url) {
                if (skipCache) {
                    headers.append(HttpHeaders.CacheControl, "no-cache")
                }
            }
        }
        if (result.isFailure()) return +result
        val response = result.value
        if (!response.status.isSuccess()) return +GithubException.NonSuccessResponse(response.status.value)

        return tryCatch { response.bodyAsText() }
    }

    suspend fun fetchReleaseByTagName(
        owner: String,
        repo: String,
        tagName: String
    ) = withContext(dispatcher) {
        val result = tryCatch {
            client.get(urlString = "https://api.github.com/repos/$owner/$repo/releases/tags/$tagName") {
                headers.append(HttpHeaders.Accept, "application/vnd.github+json")
                headers.append("X-GitHub-Api-Version", "2026-03-10")

//                if (skipCache) {
//                    headers.append(HttpHeaders.CacheControl, "no-cache")
//                }
            }
        }
        if (result.isFailure()) return@withContext null
        val response = result.value
        if (!response.status.isSuccess()) return@withContext null

        val bodyResult = tryCatch { response.body<Release>() }
        if (bodyResult.isFailure()) return@withContext null

        bodyResult.value
    }

    suspend fun fetchReleases(
        owner: String,
        repo: String,
    ) {
        val result = tryCatch {
            client.get(urlString = "https://api.github.com/repos/$owner/$repo/releases") {
                headers.append(HttpHeaders.Accept, "application/vnd.github+json")
                headers.append("X-GitHub-Api-Version", "2026-03-10")
            }
        }

    }
}

abstract class GithubException : Exception() {
    class NonSuccessResponse(val code: Int) : GithubException()
}
