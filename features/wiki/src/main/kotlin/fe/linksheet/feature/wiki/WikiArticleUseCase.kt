package fe.linksheet.feature.wiki

import fe.httpkt.Request
import fe.httpkt.ext.isHttpSuccess
import fe.httpkt.ext.readToString
import fe.linksheet.feature.wiki.database.repository.WikiCacheRepository
import fe.linksheet.util.CacheResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WikiArticleUseCase(
    val request: Request,
    val repository: WikiCacheRepository,
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private fun fetchText(url: String): String? {
        val response = request.get(url = url)
        if (!response.isHttpSuccess()) return null

        return response.readToString()
    }

    suspend fun getWikiText(url: String): String? = withContext(ioDispatcher) {
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
