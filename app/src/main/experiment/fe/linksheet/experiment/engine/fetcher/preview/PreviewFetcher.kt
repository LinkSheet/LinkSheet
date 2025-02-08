package fe.linksheet.experiment.engine.fetcher.preview

import fe.linksheet.experiment.engine.fetcher.FetchInput
import fe.linksheet.experiment.engine.fetcher.FetchOutput
import fe.linksheet.experiment.engine.fetcher.LinkFetcher
import fe.linksheet.module.repository.CacheRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.saket.unfurl.Unfurler

class PreviewFetcher(
    private val source: PreviewSource,
    private val cacheRepository: CacheRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val localCache: () -> Boolean,
) : LinkFetcher {

    override suspend fun resolve(data: FetchInput): FetchOutput? {
        val localCache = localCache()
        val entry = cacheRepository.getOrCreateCacheEntry(data.url)
        if(localCache) {
            val previewCache = cacheRepository.getCachedPreview(entry.id)
            if(previewCache != null) {

            }

            cacheRepository.getCachedHtml(entry.id)
        }

        val result = source.fetch(data.url)

        TODO()

//        return withContext(dispatcher) {
//            unfurler.unfurl(data.url)
//            FetchOutput(data.url)
//        }
    }
}
