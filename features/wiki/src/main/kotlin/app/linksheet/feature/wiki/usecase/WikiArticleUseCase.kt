package app.linksheet.feature.wiki.usecase

import app.linksheet.feature.wiki.database.entity.WikiCache
import app.linksheet.feature.wiki.database.repository.WikiCacheRepository
import fe.linksheet.util.CacheResult
import fe.linksheet.util.StoredCacheResult
import fe.std.result.isFailure
import fe.std.result.tryCatch
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WikiArticleUseCase internal constructor(
    private val client: HttpClient,
    private val repository: WikiCacheRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private suspend fun fetchText(
        url: String,
        skipCache: Boolean
    ): String? = withContext(dispatcher) {
        val result = tryCatch {
            client.get(urlString = url) {
                if (skipCache) {
                    headers.append(HttpHeaders.CacheControl, "no-cache")
                }
            }
        }
        if (result.isFailure()) return@withContext null
        val response = result.value
        if (!response.status.isSuccess()) return@withContext null

        return@withContext response.bodyAsText()
    }

    suspend fun getWikiText(
        url: String,
        skipCache: Boolean
    ): String? = withContext(dispatcher) {
        var cacheResult: CacheResult<WikiCache>? = null
        if (!skipCache) {
            cacheResult = repository.getCached(url)
            if (cacheResult is StoredCacheResult.Hit) {
                return@withContext cacheResult.value.text
            }
        }

        val text = fetchText(url, skipCache)
        if (text != null) {
            if (cacheResult is StoredCacheResult<WikiCache>) {
                repository.update(cacheResult.value, text)
            } else {
                repository.insert(url, text)
            }
        }

        return@withContext text
    }
}
