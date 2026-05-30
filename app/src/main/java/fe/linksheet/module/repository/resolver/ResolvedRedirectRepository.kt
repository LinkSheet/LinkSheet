package fe.linksheet.module.repository.resolver

import fe.linksheet.module.database.dao.resolver.ResolvedRedirectDao
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
import kotlinx.coroutines.flow.Flow

class ResolvedRedirectRepository(private val dao: ResolvedRedirectDao) {
    fun getAll(): Flow<List<ResolvedRedirect>> {
        return dao.getAll()
    }

    suspend fun insert(inputUrl: String, resolvedUrl: String): ResolvedRedirect {
        val item = ResolvedRedirect(inputUrl, resolvedUrl)
        dao.insert(item)
        return item
    }

    fun getForInputUrl(inputUrl: String): Pair<ResolvedRedirect, String?>? {
        val item = dao.getForInputUrl(inputUrl)
        return item?.let { it to it.resolvedUrl }
    }
}
