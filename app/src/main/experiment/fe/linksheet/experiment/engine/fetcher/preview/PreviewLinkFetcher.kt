package fe.linksheet.experiment.engine.fetcher.preview

import fe.linksheet.experiment.engine.fetcher.LinkFetcher
import fe.linksheet.experiment.engine.fetcher.LinkFetcherId
import fe.linksheet.module.database.entity.cache.PreviewCache
import fe.linksheet.module.repository.CacheRepository
import fe.std.result.isFailure
import fe.std.uri.StdUrl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

data class PreviewLinkFetcher(
    private val source: PreviewSource,
    private val cacheRepository: CacheRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val useLocalCache: () -> Boolean,
) : LinkFetcher<PreviewFetchResult> {
    override val id = LinkFetcherId.Preview

    private suspend fun insertCache(entryId: Long, result: PreviewFetchResult) {
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

    private suspend fun handleCached(entryId: Long, url: String): PreviewFetchResult? {
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

    override suspend fun fetch(url: StdUrl): PreviewFetchResult? {
        val localCache = useLocalCache()
        val entry = cacheRepository.getOrCreateCacheEntry(url.toString())
        if (localCache) {
            val cachedPreview = handleCached(entry.id, entry.url)
            if (cachedPreview != null) {
                return cachedPreview
            }
        }

        val result = source.fetch(entry.url)
        if (result.isFailure()) {
            return PreviewFetchResult.NoPreview(entry.url)
        }

        if (localCache) {
            insertCache(entry.id, result.value)
        }

        return result.value
    }
}
