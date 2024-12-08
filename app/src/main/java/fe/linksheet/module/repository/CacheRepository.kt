package fe.linksheet.module.repository

import fe.linksheet.module.database.dao.cache.*
import fe.linksheet.module.database.entity.cache.ResolveType

class CacheRepository(
    val htmlCacheDao: HtmlCacheDao,
    val previewCacheDao: PreviewCacheDao,
    val resolvedUrlCacheDao: ResolvedUrlCacheDao,
    val resolveTypeDao: ResolveTypeDao,
    val urlEntityDao: UrlEntryDao
) {
    suspend fun checkCache(url: String, resolveType: ResolveType): CacheData? {
        val entry = urlEntityDao.getCacheEntry(url) ?: return null

        val resolved = resolvedUrlCacheDao.getResolved(entry.id, resolveType.id)
        val html = htmlCacheDao.getCachedHtml(entry.id)

        return CacheData(resolveType, resolved?.result, html?.content)
    }
}

class CacheData(
    val type: ResolveType,
    val resolved: String?,
    val html: String?
)
