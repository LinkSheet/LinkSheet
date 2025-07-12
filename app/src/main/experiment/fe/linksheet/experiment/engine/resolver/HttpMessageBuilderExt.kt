package fe.linksheet.experiment.engine.resolver

import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMessageBuilder
import me.saket.unfurl.extension.HtmlMetadataUnfurlerExtension

fun HttpMessageBuilder.configureHeaders(
    httpUserAgent: String = HtmlMetadataUnfurlerExtension.SlackBotUserAgent,
    htmlByteLimit: Long? = 32_768
) {
    header(HttpHeaders.UserAgent, httpUserAgent)
    header(HttpHeaders.Accept, "text/html")
    header(HttpHeaders.AcceptLanguage, "en-US,en;q=0.5")
    htmlByteLimit?.let {
        header(HttpHeaders.Range, "bytes=0-$it")
    }
}
