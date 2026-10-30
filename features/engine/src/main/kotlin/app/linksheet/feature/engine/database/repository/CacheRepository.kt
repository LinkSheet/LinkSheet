package app.linksheet.feature.engine.database.repository

import app.linksheet.feature.engine.core.fetcher.preview.HtmlPreviewResult
import app.linksheet.feature.engine.core.fetcher.preview.PreviewFetchResult
import app.linksheet.feature.engine.core.fetcher.preview.PreviewFetchResultId
import app.linksheet.feature.engine.database.dao.*
import app.linksheet.feature.engine.database.entity.*
import kotlin.time.Clock

class CacheRepository internal constructor(
    val htmlCacheDao: HtmlCacheDao,
    val previewCacheDao: PreviewCacheDao,
    val resolvedUrlCacheDao: ResolvedUrlCacheDao,
    val resolveTypeDao: ResolveTypeDao,
    val urlEntryDao: UrlEntryDao,
    val clock: Clock,
    private val incognitoMode: () -> Boolean
) {
    suspend fun eraseAll() {
        urlEntryDao.deleteAll()
    }
    suspend fun getOrCreateCacheEntry(url: String): UrlEntry? {
        val entry = urlEntryDao.getUrlEntry(url)
        if (entry != null) return entry

        return createUrlEntry(url)
    }

    suspend fun getCacheEntry(url: String): UrlEntry? {
        return urlEntryDao.getUrlEntry(url)
    }

    suspend fun getResolved(entryId: Long, resolveType: ResolveType): ResolvedUrl? {
        return resolvedUrlCacheDao.getResolved(entryId, resolveType.id)
    }

    suspend fun getCachedHtml(entryId: Long): CachedHtml? {
        return htmlCacheDao.getCachedHtml(entryId)
    }

    suspend fun getCachedPreview(entryId: Long): PreviewCache? {
        return previewCacheDao.getPreviewCache(entryId)
    }

    @Deprecated(message = "Use new API")
    suspend fun checkCache(url: String, resolveType: ResolveType): CacheData? {
        val entry = urlEntryDao.getUrlEntry(url) ?: return null

        val resolved = resolvedUrlCacheDao.getResolved(entry.id, resolveType.id)
        val html = htmlCacheDao.getCachedHtml(entry.id)

        return CacheData(resolveType, resolved?.result, html?.content)
    }

    suspend fun createUrlEntry(url: String): UrlEntry? {
        if (incognitoMode()) return null
        val now = clock.now()
        val entry = UrlEntry(timestamp = now.toEpochMilliseconds(), url = url)
        val id = urlEntryDao.insertReplace(entry)

        entry.id = id
        return entry
    }

    suspend fun insertResolved(entryId: Long, resolveType: ResolveType, resolvedUrl: String?) {
        if (incognitoMode()) return
        resolvedUrlCacheDao.insertReplace(ResolvedUrl(entryId, resolveType.id, resolvedUrl))
    }

    suspend fun insertHtml(entryId: Long, html: String) {
        if (incognitoMode()) return
        htmlCacheDao.insertReplace(CachedHtml(entryId, html))
    }

    suspend fun insertPreview(entryId: Long, result: PreviewFetchResult) {
        if (incognitoMode()) return
        if (!result.id.hasPreview()) {
            // TODO: Should we cache the info that there has not been a cache hit?
            // TODO: Caching non-hits would obviously help an unnecessary round-trip to the remote host, but what would that mean
            // for situations where the preview is added at a later date? We would probably have to use some sort of TTL
            return
        }

        val cacheEntry = when (result) {
            is HtmlPreviewResult.Rich -> PreviewCache(
                resultId = PreviewFetchResultId.Rich,
                id = entryId,
                title = result.title,
                description = result.description,
                faviconUrl = result.favicon,
                thumbnailUrl = result.thumbnail
            )

            is HtmlPreviewResult.Simple -> PreviewCache(
                resultId = PreviewFetchResultId.Simple,
                id = entryId,
                title = result.title,
                description = null,
                faviconUrl = result.favicon,
                thumbnailUrl = null
            )

            else -> throw Exception("Unreachable")
        }

        previewCacheDao.insertReplace(cacheEntry)
    }
}

class CacheData(
    val type: ResolveType,
    val resolved: String?,
    val html: String?
)
