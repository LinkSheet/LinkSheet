package fe.linksheet.experiment.engine.resolver.amp2html

import fe.amp2htmlkt.Amp2Html
import fe.linksheet.experiment.engine.resolver.configureHeaders
import fe.linksheet.extension.ktor.isHtml
import fe.std.result.*
import fe.std.uri.StdUrl
import fe.std.uri.toStdUrlOrThrow
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class Amp2HtmlLocalSource(private val client: HttpClient) : Amp2HtmlSource {

    override suspend fun resolve(url: StdUrl): IResult<Amp2HtmlResult> {
        val result = tryCatch {
            client.get(urlString = url.toString()) { configureHeaders() }
        }
        if (result.isFailure()) {
            return +result
        }

        val response = result.value
        if (!response.isHtml()) {
            return +Amp2HtmlResult.NothingToResolve(url)
        }

        val htmlText = response.bodyAsText()
        return parseHtml(htmlText, url)
    }

    override suspend fun parseHtml(htmlText: String, url: StdUrl): IResult<Amp2HtmlResult> {
        val result = tryCatch { Amp2Html.getNonAmpLink(htmlText, url.toString()) }
        if (result.isFailure()) {
            return +Amp2HtmlResult.NothingToResolve(url)
        }

        val nonAmpLink = result.value
        if (nonAmpLink != null) {
            return +Amp2HtmlResult.NonAmpLink(nonAmpLink.toStdUrlOrThrow(), htmlText)
        }

        return +Amp2HtmlResult.NothingToResolve(url)
    }
}
