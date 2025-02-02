package fe.linksheet.experiment.engine.resolver.amp2html

import fe.amp2htmlkt.Amp2Html
import fe.linksheet.experiment.engine.resolver.configureHeaders
import fe.linksheet.extension.ktor.isHtml
import fe.std.result.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class Amp2HtmlLocalSource(private val client: HttpClient) : Amp2HtmlSource {

    override suspend fun resolve(urlString: String): IResult<Amp2HtmlResult> {
        val result = tryCatch {
            client.get(urlString = urlString) { configureHeaders() }
        }
        if (result.isFailure()) {
            return +result
        }

        val response = result.value
        if (!response.isHtml()) {
            return Amp2HtmlResult.NothingToResolve(urlString).success
        }

        val htmlText = response.bodyAsText()
        return parseHtml(htmlText, urlString)
    }

    override suspend fun parseHtml(htmlText: String, urlString: String): IResult<Amp2HtmlResult> {
        val result = tryCatch { Amp2Html.getNonAmpLink(htmlText, urlString) }
        if (result.isFailure()) {
            return Amp2HtmlResult.NothingToResolve(urlString).success
        }

        val nonAmpLink = result.value
        if (nonAmpLink != null) {
            return Amp2HtmlResult.NonAmpLink(nonAmpLink, htmlText).success
        }

        return Amp2HtmlResult.NothingToResolve(urlString).success
    }
}
