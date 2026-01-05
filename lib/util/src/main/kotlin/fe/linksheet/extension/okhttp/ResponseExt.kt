package fe.linksheet.extension.okhttp

import fe.linksheet.web.JsoupHelper
import fe.linksheet.web.isHtml
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import okhttp3.Response
import org.jsoup.nodes.Document

fun Response.contentType(): ContentType? {
    return headers[HttpHeaders.ContentType]?.let { ContentType.parse(it) }
}

fun Response.charset(): Charset? {
    return contentType()?.charset()
}

fun Response.isHtml(): Boolean {
    return contentType().isHtml()
}

fun Response.urlString(): String {
    return request.url.toString()
}

fun Response.refresh(): String? {
    return headers["refresh"]
}

fun Response.parseHtmlBody(): Document {
    return JsoupHelper.parseHeadAsStream(body.byteStream(), urlString(), charset())
}
