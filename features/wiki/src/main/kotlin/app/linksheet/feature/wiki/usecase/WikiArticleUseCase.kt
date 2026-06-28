package app.linksheet.feature.wiki.usecase

import app.linksheet.feature.wiki.database.entity.WikiCache
import app.linksheet.feature.wiki.database.repository.WikiCacheRepository
import fe.linksheet.util.CacheResult
import fe.linksheet.util.StoredCacheResult
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WikiArticleUseCase internal constructor(
    private val client: HttpClient,
    private val repository: WikiCacheRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
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

        val text = getWikiText(url, skipCache)
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
