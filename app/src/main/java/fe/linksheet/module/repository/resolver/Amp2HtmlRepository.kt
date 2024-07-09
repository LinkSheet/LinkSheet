package fe.linksheet.module.repository.resolver

import fe.linksheet.module.database.dao.resolver.Amp2HtmlMappingDao
import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping

class Amp2HtmlRepository(dao: Amp2HtmlMappingDao) : ResolverRepository<Amp2HtmlMapping>(dao, "canonicalUrl") {

    override suspend fun insert(inputUrl: String, resolvedUrl: String) = dao.insert(
        Amp2HtmlMapping(inputUrl, resolvedUrl)
    )
}
