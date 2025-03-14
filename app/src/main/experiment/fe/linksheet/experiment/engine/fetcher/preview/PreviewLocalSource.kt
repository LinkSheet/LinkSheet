package fe.linksheet.experiment.engine.fetcher.preview

import fe.linksheet.experiment.engine.resolver.configureHeaders
import fe.linksheet.extension.ktor.isHtml
import fe.std.result.IResult
import fe.std.result.isFailure
import fe.std.result.success
import fe.std.result.tryCatch
import fe.std.result.unaryPlus
import io.ktor.client.*
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class PreviewLocalSource(
    private val client: HttpClient,
    private val htmlMetadataParser: HtmlMetadataParser = HtmlMetadataParser()
) : PreviewSource {

    override suspend fun fetch(urlString: String): IResult<PreviewResult> {
        val result = tryCatch {
            client.get(urlString = urlString) { configureHeaders() }
        }

        if (result.isFailure()) {
            return +result
        }

        val response = result.value
        if (!response.isHtml()) {
            return PreviewResult.NonHtmlPage(urlString).success
        }

        val htmlText = response.bodyAsText()
        return parseHtml(htmlText, urlString)
    }

    override suspend fun parseHtml(htmlText: String, urlString: String): IResult<PreviewResult> {
        val result = tryCatch { htmlMetadataParser.parse(htmlText, urlString) }

        return result
    }
}


