package fe.linksheet.experiment.engine.fetcher.preview

import fe.linksheet.AndroidLogger
import fe.linksheet.Logger
import fe.linksheet.experiment.engine.resolver.configureHeaders
import fe.linksheet.extension.ktor.isHtml
import fe.std.result.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.contentType

class PreviewLocalSource(
    private val client: HttpClient,
    private val logger: Logger = AndroidLogger<PreviewLocalSource>(),
    private val htmlMetadataParser: HtmlMetadataParser = HtmlMetadataParser()
) : PreviewSource {

    override suspend fun fetch(urlString: String): IResult<PreviewFetchResult> {
        val result = tryCatch {
            client.get(urlString = urlString) { configureHeaders() }
        }

        if (result.isFailure()) {
            return +result
        }

        val response = result.value
        logger.debug { "Response: $response, ${response.contentType()}, ${response.isHtml()}" }
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


