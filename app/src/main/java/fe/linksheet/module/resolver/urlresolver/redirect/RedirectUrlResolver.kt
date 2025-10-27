package fe.linksheet.module.resolver.urlresolver.redirect

import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
import app.linksheet.feature.engine.database.repository.CacheRepository
import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
import fe.linksheet.module.resolver.urlresolver.base.UrlResolver

class RedirectUrlResolver(
    redirectResolver: RedirectResolveRequest,
    resolverRepository: ResolvedRedirectRepository,
    cacheRepository: CacheRepository
) : UrlResolver<ResolvedRedirect, RedirectUrlResolver>(
    RedirectUrlResolver::class,
    redirectResolver,
    resolverRepository,
    cacheRepository
)
