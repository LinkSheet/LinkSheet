package fe.linksheet.module.repository.resolver

import fe.cachedurls.CachedUrlsLoader
import fe.cachedurls.getResolvedUrlMap
import fe.linksheet.module.database.dao.resolver.ResolvedRedirectDao
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect

class ResolvedRedirectRepository(
    dao: ResolvedRedirectDao
) : ResolverRepository<ResolvedRedirect>(dao, "resolvedUrl") {

    private val redirectCacheObject by lazy {
        getResolvedUrlMap(CachedUrlsLoader.loadCachedUrlsJson(CachedUrlsResource.getBuiltInResolvedUrlsJson()!!))
    }

    override fun getBuiltInCachedForUrl(inputUrl: String) = redirectCacheObject[inputUrl]


    override suspend fun insert(inputUrl: String, resolvedUrl: String) = dao.insert(
        ResolvedRedirect(inputUrl, resolvedUrl)
    )
}