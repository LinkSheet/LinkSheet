package fe.linksheet.extension.ktor

import fe.linksheet.web.JsoupHelper
import fe.linksheet.web.isHtml
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

fun HttpMessage.contentDisposition(): ContentDisposition? {
    return headers[HttpHeaders.ContentDisposition]?.let { ContentDisposition.parse(it) }
}

suspend fun HttpResponse.parseHeadAsStream(): Document {
    return JsoupHelper.parseHeadAsStream(bodyAsChannel().toInputStream(), urlString(), charset())
}
