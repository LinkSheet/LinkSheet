package app.linksheet.feature.wiki.usecase

import app.linksheet.feature.wiki.database.repository.WikiCacheRepository
import fe.linksheet.util.CacheResult
import fe.std.result.isFailure
import fe.std.result.tryCatch
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WikiArticleUseCase internal constructor(
    private val client: HttpClient,
    private val repository: WikiCacheRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private suspend fun fetchText(url: String): String? {
        val result = tryCatch { client.get(urlString = url) }
        if (result.isFailure()) return null
        val response = result.value
        if (!response.status.isSuccess()) return null

        return response.bodyAsText()
    }

    suspend fun getWikiText(url: String): String? = withContext(dispatcher) {
        val cacheResult = repository.getCached(url)
        if (cacheResult is CacheResult.Hit) {
            return@withContext cacheResult.value.text
        }

        val text = fetchText(url)
        if (text != null) {
            if (cacheResult is CacheResult.Stale) {
                repository.update(cacheResult.value, text)
            } else {
                repository.insert(url, text)
            }
        }

        return@withContext text
    }
}
