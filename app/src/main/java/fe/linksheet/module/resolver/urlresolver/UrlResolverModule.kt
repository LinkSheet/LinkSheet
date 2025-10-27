package fe.linksheet.module.resolver.urlresolver

import app.linksheet.api.CachedRequest
import fe.composekit.preference.asFunction
import fe.droidkit.koin.single
import fe.httpkt.Request
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import app.linksheet.feature.engine.database.repository.CacheRepository
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlResolveRequest
import fe.linksheet.module.resolver.urlresolver.base.AllRemoteResolveRequest
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectResolveRequest
import fe.linksheet.util.buildconfig.LinkSheetAppConfig
import okhttp3.OkHttpClient
import org.koin.dsl.module

val UrlResolverModule = module {
    val host = LinkSheetAppConfig.supabaseHost()
    val token = LinkSheetAppConfig.supabaseApiKey()
    single<RedirectResolveRequest, Request, CachedRequest> { _, request, cachedRequest ->
        val experimentRepository = scope.get<ExperimentRepository>()
        RedirectResolveRequest(
            apiUrl = "${host}/redirector",
            token = token,
            request = request,
            urlResolverCache = cachedRequest,
            okHttpClient = scope.get<OkHttpClient>(),
            aggressiveExperiment = experimentRepository.asFunction(Experiments.aggressiveFollowRedirects)
        )
    }
    single<Amp2HtmlResolveRequest, Request, CachedRequest> { _, request, cachedRequest ->
        Amp2HtmlResolveRequest(
            apiUrl = "${host}/amp2html",
            token = token,
            request = request,
            cacheRepository = scope.get<CacheRepository>(),
            urlResolverCache = cachedRequest,
            okHttpClient = scope.get<OkHttpClient>()
        )
    }
    single<AllRemoteResolveRequest, Request> { _, request ->
        AllRemoteResolveRequest(
            apiUrl = "${host}/all",
            token = token,
            request = request
        )
    }
}
