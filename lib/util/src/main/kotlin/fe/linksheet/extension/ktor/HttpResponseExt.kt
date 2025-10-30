package fe.linksheet.extension.ktor

import fe.linksheet.web.isHtml
import fe.linksheet.web.parseHtmlBody
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import org.jsoup.nodes.Document

fun HttpResponse.isHtml(): Boolean {
    return contentType().isHtml()
}

fun HttpResponse.urlString(): String {
    return request.url.toString()
}

fun HttpResponse.refresh(): String? {
    return headers["refresh"]
}

suspend fun HttpResponse.parseHtmlBody(): Document {
    return bodyAsChannel().toInputStream().use { it.parseHtmlBody(urlString(), charset()) }
}
