package fe.linksheet.module.repository

import fe.linksheet.module.database.dao.cache.*
import fe.linksheet.module.database.entity.cache.CachedHtml
import fe.linksheet.module.database.entity.cache.ResolveType
import fe.linksheet.module.database.entity.cache.ResolvedUrl
import fe.linksheet.module.database.entity.cache.UrlEntry

class CacheRepository(
    val htmlCacheDao: HtmlCacheDao,
    val previewCacheDao: PreviewCacheDao,
    val resolvedUrlCacheDao: ResolvedUrlCacheDao,
    val resolveTypeDao: ResolveTypeDao,
    val urlEntryDao: UrlEntryDao,
    private val now: () -> Long = { System.currentTimeMillis() }
) {

    suspend fun getOrCreateCacheEntry(url: String): UrlEntry {
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

    @Deprecated(message = "Use new API")
    suspend fun checkCache(url: String, resolveType: ResolveType): CacheData? {
        val entry = urlEntryDao.getUrlEntry(url) ?: return null

        val resolved = resolvedUrlCacheDao.getResolved(entry.id, resolveType.id)
        val html = htmlCacheDao.getCachedHtml(entry.id)

        return CacheData(resolveType, resolved?.result, html?.content)
    }

    suspend fun createUrlEntry(url: String): UrlEntry {
        val entry = UrlEntry(timestamp = now(), url = url)
        val id = urlEntryDao.insertReturningId(entry)

        entry.id = id
        return entry
    }

    suspend fun insertResolved(entryId: Long, resolveType: ResolveType, resolvedUrl: String?) {
        resolvedUrlCacheDao.insert(ResolvedUrl(entryId, resolveType.id, resolvedUrl))
    }

    suspend fun insertHtml(entryId: Long, html: String) {
        htmlCacheDao.insert(CachedHtml(entryId, html))
    }
}

class CacheData(
    val type: ResolveType,
    val resolved: String?,
    val html: String?
)
