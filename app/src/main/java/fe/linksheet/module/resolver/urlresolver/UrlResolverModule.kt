package fe.linksheet.module.resolver.urlresolver

import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlResolveRequest
import fe.linksheet.module.resolver.urlresolver.base.LocalTask
import fe.linksheet.module.resolver.urlresolver.base.UrlResolver
import fe.linksheet.module.resolver.urlresolver.redirect.AggressiveRedirectResolveRedirect
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectResolveRequest
import fe.linksheet.util.buildconfig.LinkSheetAppConfig
import org.koin.dsl.module

val UrlResolverModule = module {
    val host = LinkSheetAppConfig.supabaseHost()
    val token = LinkSheetAppConfig.supabaseApiKey()
    single {
        val resolvedRedirectRepository = get<ResolvedRedirectRepository>()
        val amp2HtmlRepository= get<Amp2HtmlRepository>()
        UrlResolver(
            redirectorTask = LocalTask.Redirector(
                request = RedirectResolveRequest(httpClient = get()),
                getForInputUrl = resolvedRedirectRepository::getForInputUrl,
                insert = resolvedRedirectRepository::insert
            ),
            aggressiveRedirectorTask = LocalTask.Redirector(
                request = AggressiveRedirectResolveRedirect(httpClient = get()),
                getForInputUrl = resolvedRedirectRepository::getForInputUrl,
                insert = resolvedRedirectRepository::insert
            ),
            amp2HtmlTask = LocalTask.Amp2Html(
                request = Amp2HtmlResolveRequest(httpClient = get()),
                getForInputUrl = amp2HtmlRepository::getForInputUrl,
                insert = amp2HtmlRepository::insert
            ),
            remoteResolver = RemoteResolver(
                apiHost = host,
                token = token,
                httpClient = get(),
            ),
            cacheRepository = get()
        )
    }
}
