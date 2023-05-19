package fe.linksheet.module.repository

import fe.linksheet.data.dao.ResolvedRedirectDao
import fe.linksheet.data.entity.ResolvedRedirect

class ResolvedRedirectRepository(private val dao: ResolvedRedirectDao) {
    fun getForShortUrl(shortUrl: String) = dao.getForShortUrl(shortUrl)
    suspend fun insert(resolvedRedirect: ResolvedRedirect) = dao.insert(resolvedRedirect)
}
