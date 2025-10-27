package fe.linksheet.module.resolver.urlresolver.amp2html

import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping
import app.linksheet.feature.engine.database.repository.CacheRepository
import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
import fe.linksheet.module.resolver.urlresolver.base.UrlResolver

class Amp2HtmlUrlResolver(
    redirectResolver: Amp2HtmlResolveRequest,
    resolverRepository: Amp2HtmlRepository,
    cacheRepository: CacheRepository
) : UrlResolver<Amp2HtmlMapping, Amp2HtmlUrlResolver>(
    Amp2HtmlUrlResolver::class,
    redirectResolver,
    resolverRepository,
    cacheRepository
)
