package fe.linksheet.experiment.engine.fetcher.preview

import fe.linksheet.experiment.engine.fetcher.FetchInput
import fe.linksheet.experiment.engine.fetcher.FetchOutput
import fe.linksheet.experiment.engine.fetcher.LinkFetcher
import fe.linksheet.module.database.entity.cache.PreviewCache
import fe.linksheet.module.repository.CacheRepository
import fe.std.result.isFailure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class PreviewLinkFetcher(
    private val source: PreviewSource,
    private val cacheRepository: CacheRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val useLocalCache: () -> Boolean,
) : LinkFetcher<PreviewResult> {

    private suspend fun insertCache(entryId: Long, result: PreviewResult) {
        cacheRepository.insertPreview(entryId, result)
        if (result is HtmlPreviewResult) {
            cacheRepository.insertHtml(entryId, result.htmlText)
        }
    }

    private fun restoreResult(url: String, previewCache: PreviewCache): HtmlPreviewResult {
        if (previewCache.thumbnailUrl == null && previewCache.description == null) {
            return HtmlPreviewResult.SimplePreviewResult(
                url = url,
                htmlText = "",
                title = previewCache.title,
                favicon = previewCache.faviconUrl
            )
        }

        return HtmlPreviewResult.RichPreviewResult(
            url = url,
            htmlText = "",
            title = previewCache.title,
            description = previewCache.description,
            favicon = previewCache.faviconUrl,
            thumbnail = previewCache.thumbnailUrl
        )
    }

    override suspend fun fetch(data: FetchInput): PreviewResult? {
        val localCache = useLocalCache()
        val entry = cacheRepository.getOrCreateCacheEntry(data.url)
        if (localCache) {
            val previewCache = cacheRepository.getCachedPreview(entry.id)
            if (previewCache != null) {
                return restoreResult(data.url, previewCache)
            }

            val cachedHtml = cacheRepository.getCachedHtml(entry.id)
            if(cachedHtml != null) {
               source.parseHtml(cachedHtml.content, data.url)
            }
        }

        val result = source.fetch(data.url)
        if (result.isFailure()) {
            TODO("Maybe we should return IResult from LinKFetcher's fetch(...) instead?")
//            return FetchOutput(data.url)
        }

        if (localCache) {
            insertCache(entry.id, result.value)
        }


        TODO()

//        return withContext(dispatcher) {
//            unfurler.unfurl(data.url)
//            FetchOutput(data.url)
//        }
    }
}
