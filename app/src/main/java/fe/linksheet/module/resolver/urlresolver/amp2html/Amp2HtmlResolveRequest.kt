package fe.linksheet.module.resolver.urlresolver.amp2html

import fe.amp2htmlkt.Amp2Html
import fe.httpkt.HttpData
import fe.httpkt.Request
import fe.httpkt.isHttpSuccess
import fe.httpkt.util.getGZIPOrDefaultStream
import fe.linksheet.extension.createLogger
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.UrlProcessor
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequest
import fe.linksheet.supabaseApiKey
import fe.linksheet.supabaseFunctionHost
import org.koin.dsl.module
import java.io.IOException
import java.net.URL

val amp2HtmlResolveRequestModule = module {
    single {
        Amp2HtmlResolveRequest(
            "$supabaseFunctionHost/amp2html",
            supabaseApiKey,
            get(),
            createLogger<Amp2HtmlResolveRequest>()

        )
    }
}

class Amp2HtmlResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
    logger: Logger
) : ResolveRequest(apiUrl, token, request, logger) {

    @Throws(IOException::class)
    override fun resolveLocal(url: String, timeout: Int): String? {
        logger.debug("ResolveLocal %s", url, UrlProcessor)
        val con = request.get(
            url,
            connectTimeout = timeout * 1000,
            readTimeout = timeout * 1000,
            data = HttpData.of {
                headers {
                    "Host"(URL(url).host)
                }
            })

        if (!isHttpSuccess(con.responseCode)) {
            logger.debug("Response code ${con.responseCode} does not indicate success")
            return null
        }

        return con.getGZIPOrDefaultStream().use {
            Amp2Html.getNonAmpLink(it, URL(url).host)
        }
    }
}