package fe.linksheet.module.resolver.urlresolver

import fe.composekit.preference.asFunction
import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlResolveRequest
import fe.linksheet.module.resolver.urlresolver.base.UrlResolver
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectResolveRequest
import fe.linksheet.util.buildconfig.LinkSheetAppConfig
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

val UrlResolverModule = module {
    val host = LinkSheetAppConfig.supabaseHost()
    val token = LinkSheetAppConfig.supabaseApiKey()
    single<RedirectResolveRequest> {
        val experimentRepository = get<ExperimentRepository>()
        RedirectResolveRequest(
            httpClient = get(),
            aggressiveExperiment = experimentRepository.asFunction(Experiments.aggressiveFollowRedirects)
        )
    }
    single<Amp2HtmlResolveRequest> {
        Amp2HtmlResolveRequest(httpClient = get())
    }
    single<RemoteResolveRequest>(qualifier(RemoteTask.All)) {
        RemoteResolveRequest(
            apiHost = host,
            token = token,
            task = RemoteTask.All,
            httpClient = get(),
            "redirect", "amp2html"
        )
    }
    single<RemoteResolveRequest>(qualifier(RemoteTask.Redirector)) {
        RemoteResolveRequest(
            apiHost = host,
            token = token,
            task = RemoteTask.Redirector,
            httpClient = get(),
            "redirect",
        )
    }
    single<RemoteResolveRequest>(qualifier(RemoteTask.Amp2Html)) {
        RemoteResolveRequest(
            apiHost = host,
            token = token,
            task = RemoteTask.Amp2Html,
            httpClient = get(),
            "amp2html",
        )
    }
    single {
        UrlResolver<Amp2HtmlMapping, Amp2HtmlResolveRequest, Amp2HtmlRepository>(
            clazz = Amp2HtmlResolveRequest::class,
            localRequest = Amp2HtmlResolveRequest(httpClient = get()),
            remoteRequest = get(qualifier(RemoteTask.Amp2Html)),
            resolverRepository = get(),
            cacheRepository = get()
        )
    }
    single {
        val experimentRepository = get<ExperimentRepository>()
        UrlResolver<ResolvedRedirect, RedirectResolveRequest, ResolvedRedirectRepository>(
            clazz = RedirectResolveRequest::class,
            localRequest = RedirectResolveRequest(
                httpClient = get(),
                aggressiveExperiment = experimentRepository.asFunction(Experiments.aggressiveFollowRedirects)
            ),
            remoteRequest = get(qualifier(RemoteTask.Redirector)),
            resolverRepository = get(),
            cacheRepository = get()
        )
    }
}
