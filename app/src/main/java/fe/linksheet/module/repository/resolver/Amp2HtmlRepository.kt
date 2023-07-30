package fe.linksheet.module.repository.resolver

import fe.cachedurls.CachedUrlsLoader
import fe.cachedurls.getResolvedUrlMap
import fe.linksheet.module.database.dao.resolver.Amp2HtmlMappingDao
import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping

class Amp2HtmlRepository(
    dao: Amp2HtmlMappingDao
) : ResolverRepository<Amp2HtmlMapping>(dao, "canonicalUrl") {

    private val amp2HtmlCache by lazy {
        getResolvedUrlMap(CachedUrlsLoader.loadCachedUrlsJson(CachedUrlsResource.getBuiltInAmp2HtmlUrlsJson()!!)!!)
    }

    override fun getBuiltInCachedForUrl(inputUrl: String) = amp2HtmlCache[inputUrl]

    override suspend fun insert(inputUrl: String, resolvedUrl: String) = dao.insert(
        Amp2HtmlMapping(inputUrl, resolvedUrl)
    )
}