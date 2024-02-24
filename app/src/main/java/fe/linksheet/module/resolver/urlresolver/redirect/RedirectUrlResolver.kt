package fe.linksheet.module.resolver.urlresolver.redirect

import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
import fe.linksheet.module.log.factory.LoggerFactory
import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
import fe.linksheet.module.resolver.urlresolver.base.UrlResolver

class RedirectUrlResolver(
    loggerFactory: LoggerFactory,
    redirectResolver: RedirectResolveRequest,
    resolverRepository: ResolvedRedirectRepository,
) : UrlResolver<ResolvedRedirect, RedirectUrlResolver>(
    loggerFactory,
    RedirectUrlResolver::class,
    redirectResolver,
    resolverRepository
)
