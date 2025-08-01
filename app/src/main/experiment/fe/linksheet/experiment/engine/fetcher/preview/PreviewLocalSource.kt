package fe.linksheet.experiment.engine.fetcher.preview

import fe.linksheet.experiment.engine.resolver.configureHeaders
import fe.linksheet.extension.ktor.isHtml
import fe.linksheet.log.Logger
import fe.std.result.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class PreviewLocalSource(
    private val client: HttpClient,
    private val htmlMetadataParser: HtmlMetadataParser = HtmlMetadataParser()
) : PreviewSource {
    private val logger = Logger("PreviewLocalSource")

    override suspend fun fetch(urlString: String): IResult<PreviewFetchResult> {
        val result = tryCatch {
            client.get(urlString = urlString) { configureHeaders() }
        }

        if (result.isFailure()) {
            return +result
        }

        val response = result.value
        if (!response.isHtml()) {
            return PreviewFetchResult.NonHtmlPage(urlString).success
        }

        val htmlText = response.bodyAsText()
        return parseHtml(htmlText, urlString)
    }

    override suspend fun parseHtml(htmlText: String, urlString: String): IResult<PreviewFetchResult> {
        val result = tryCatch { htmlMetadataParser.parse(htmlText, urlString) }

        return result
    }
}


