package fe.linksheet.experiment.engine.fetcher.preview

import fe.linksheet.experiment.engine.fetcher.FetchInput
import fe.linksheet.experiment.engine.fetcher.LinkFetcher
import fe.linksheet.module.database.entity.cache.PreviewCache
import fe.linksheet.module.repository.CacheRepository
import fe.std.result.isFailure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

data class PreviewLinkFetcher(
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

    private fun createFromCache(url: String, previewCache: PreviewCache, htmlText: String) = when {
        previewCache.isRichPreview -> HtmlPreviewResult.SimplePreviewResult(
            url = url,
            htmlText = htmlText,
            title = previewCache.title,
            favicon = previewCache.faviconUrl
        )
        else -> HtmlPreviewResult.RichPreviewResult(
            url = url,
            htmlText = htmlText,
            title = previewCache.title,
            description = previewCache.description,
            favicon = previewCache.faviconUrl,
            thumbnail = previewCache.thumbnailUrl
        )
    }

    private suspend fun handleCached(entryId: Long, url: String): PreviewResult? {
        val cachedHtml = cacheRepository.getCachedHtml(entryId)
        val previewCache = cacheRepository.getCachedPreview(entryId)
        if (cachedHtml != null && previewCache != null) {
            return createFromCache(url, previewCache, cachedHtml.content)
        }

        if (cachedHtml == null) {
            return null
        }

        val result = source.parseHtml(cachedHtml.content, url)
        if (result.isFailure()) {
            return null
        }

        insertCache(entryId, result.value)
        return result.value
    }

    override suspend fun fetch(data: FetchInput): PreviewResult? {
        val localCache = useLocalCache()
        val entry = cacheRepository.getOrCreateCacheEntry(data.url)
        if (localCache) {
            val cachedPreview = handleCached(entry.id, entry.url)
            if (cachedPreview != null) {
                return cachedPreview
            }
        }

        val result = source.fetch(entry.url)
        if (result.isFailure()) {
            return PreviewResult.NoPreview(entry.url)
        }

        if (localCache) {
            insertCache(entry.id, result.value)
        }

        return result.value
    }
}
