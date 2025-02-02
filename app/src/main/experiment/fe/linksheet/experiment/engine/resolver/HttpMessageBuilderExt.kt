package fe.linksheet.experiment.engine.resolver

import io.ktor.client.request.header
import io.ktor.http.HttpMessageBuilder
import me.saket.unfurl.extension.HtmlMetadataUnfurlerExtension

fun HttpMessageBuilder.configureHeaders(
    httpUserAgent: String = HtmlMetadataUnfurlerExtension.SlackBotUserAgent,
    htmlByteLimit: Long = 32_768
) {
    header("User-Agent", httpUserAgent)
    header("Accept", "text/html")
    header("Accept-Language", "en-US,en;q=0.5")
    header("Range", "bytes=0-$htmlByteLimit")
}
