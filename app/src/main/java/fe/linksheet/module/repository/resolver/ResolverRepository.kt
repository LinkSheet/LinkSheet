package fe.linksheet.module.repository.resolver

import fe.linksheet.module.database.dao.base.ResolverDao
import fe.linksheet.module.database.entity.resolver.ResolverEntity

abstract class ResolverRepository<T : ResolverEntity<T>>(
    protected val dao: ResolverDao<T>,
    val remoteResolveUrlField: String,
) {
    fun getForInputUrl(inputUrl: String) = dao.getForInputUrl(inputUrl)
    abstract suspend fun insert(inputUrl: String, resolvedUrl: String)
}
