package fe.linksheet.module.resolver.urlresolver.amp2html

import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
import fe.linksheet.module.resolver.urlresolver.base.UrlResolver

class Amp2HtmlUrlResolver(
    loggerFactory: LoggerFactory,
    redirectResolver: Amp2HtmlResolveRequest,
    resolverRepository: Amp2HtmlRepository,
) : UrlResolver<Amp2HtmlMapping, Amp2HtmlUrlResolver>(
    loggerFactory,
    Amp2HtmlUrlResolver::class,
    redirectResolver,
    resolverRepository
)