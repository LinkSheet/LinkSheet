package fe.linksheet.module.resolver.urlresolver.amp2html

import fe.amp2htmlkt.Amp2Html
import fe.httpkt.Request
import fe.httpkt.ext.getGZIPOrDefaultStream
import fe.httpkt.isHttpSuccess
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.extension.koin.single
import fe.linksheet.module.log.Logger
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequest
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequestException
import org.koin.dsl.module
import java.io.IOException
import java.net.URL

val amp2HtmlResolveRequestModule = module {
    single<Amp2HtmlResolveRequest, Request, CachedRequest> { _, request, cachedRequest ->
        Amp2HtmlResolveRequest(
            "${LinkSheetAppConfig.supabaseHost()}/amp2html",
            LinkSheetAppConfig.supabaseApiKey(),
            request,
            cachedRequest,
        )
    }
}

class Amp2HtmlResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
    private val urlResolverCache: CachedRequest,
) : ResolveRequest(apiUrl, token, request, "amp2html") {

    override fun resolveLocal(url: String, timeout: Int): Result<ResolveResultType> {
        val result = try {
            urlResolverCache.get(url, timeout, false)
        } catch (e: IOException) {
            return Result.failure(e)
        }

        if (!isHttpSuccess(result.responseCode)) {
            return Result.failure(ResolveRequestException(result.responseCode))
        }

        val nonAmpLink = result.getGZIPOrDefaultStream().use { Amp2Html.getNonAmpLink(it, URL(url).host) }
        if (nonAmpLink != null) {
            return Result.success(ResolveResultType.Resolved.Local(nonAmpLink))
        }

        return Result.failure(ResolveRequestException())
    }
}
