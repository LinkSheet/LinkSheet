package fe.linksheet.module.repository

import fe.linksheet.module.database.dao.ResolvedRedirectDao
import fe.linksheet.module.database.entity.ResolvedRedirect

class ResolvedRedirectRepository(private val dao: ResolvedRedirectDao) {
    fun getForShortUrl(shortUrl: String) = dao.getForShortUrl(shortUrl)
    suspend fun insert(resolvedRedirect: ResolvedRedirect) = dao.insert(resolvedRedirect)
}
