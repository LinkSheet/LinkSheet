package fe.linksheet.module.resolver.urlresolver.amp2html

import fe.amp2htmlkt.Amp2Html
import fe.httpkt.Request
import fe.httpkt.ext.getGZIPOrDefaultStream
import fe.httpkt.isHttpSuccess
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.extension.koin.single
import fe.linksheet.module.log.impl.Logger
import fe.linksheet.module.log.impl.hasher.HashProcessor
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequest
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
            serviceLogger
        )
    }
}

class Amp2HtmlResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
    private val urlResolverCache: CachedRequest,
    logger: Logger
) : ResolveRequest(apiUrl, token, request, logger, "amp2html") {

    @Throws(IOException::class)
    override fun resolveLocal(url: String, timeout: Int): String? {
        logger.debug(url, HashProcessor.UrlProcessor, { "ResolveLocal=$it" })
        val con = urlResolverCache.get(url, timeout, false)

        if (!isHttpSuccess(con.responseCode)) {
            logger.debug("Response code ${con.responseCode} does not indicate success")
            return null
        }

        return con.getGZIPOrDefaultStream().use {
            Amp2Html.getNonAmpLink(it, URL(url).host)
        }
    }
}
