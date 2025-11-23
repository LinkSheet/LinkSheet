package fe.linksheet.module.resolver.urlresolver

import fe.composekit.preference.asFunction
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlResolveRequest
import fe.linksheet.module.resolver.urlresolver.base.LocalTask
import fe.linksheet.module.resolver.urlresolver.base.UrlResolver
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectResolveRequest
import fe.linksheet.util.buildconfig.LinkSheetAppConfig
import org.koin.dsl.module

val UrlResolverModule = module {
    val host = LinkSheetAppConfig.supabaseHost()
    val token = LinkSheetAppConfig.supabaseApiKey()
    single {
        val experimentRepository = get<ExperimentRepository>()
        UrlResolver(
            redirectorTask = LocalTask.Redirector(
                request = RedirectResolveRequest(
                    httpClient = get(),
                    aggressiveExperiment = experimentRepository.asFunction(Experiments.aggressiveFollowRedirects)
                ),
                repository = get()
            ),
            amp2HtmlTask = LocalTask.Amp2Html(
                request = Amp2HtmlResolveRequest(httpClient = get()),
                repository = get()
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
