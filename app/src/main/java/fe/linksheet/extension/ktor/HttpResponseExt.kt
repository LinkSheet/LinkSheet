package fe.linksheet.extension.ktor

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.charset
import io.ktor.http.contentType
import io.ktor.utils.io.charsets.name
import io.ktor.utils.io.jvm.javaio.toInputStream
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun HttpResponse.isHtml(): Boolean {
    return ContentType.Text.Html == contentType()?.withoutParameters()
}

fun HttpResponse.urlString(): String {
    return request.url.toString()
}

suspend fun HttpResponse.parseHtmlBody(): Document {
    val document = bodyAsChannel().toInputStream().use {
        Jsoup.parse(it, charset()?.name ?: "utf-8", urlString())
    }

    return document
}
