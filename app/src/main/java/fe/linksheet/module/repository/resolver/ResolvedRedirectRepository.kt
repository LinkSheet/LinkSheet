package fe.linksheet.module.repository.resolver

import fe.linksheet.module.database.dao.resolver.ResolvedRedirectDao
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect

class ResolvedRedirectRepository(
    dao: ResolvedRedirectDao
) : ResolverRepository<ResolvedRedirect>(dao, "resolvedUrl") {


    override suspend fun insert(inputUrl: String, resolvedUrl: String) = dao.insert(
        ResolvedRedirect(inputUrl, resolvedUrl)
    )
}
