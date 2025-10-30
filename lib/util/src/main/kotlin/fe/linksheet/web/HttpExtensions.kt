package fe.linksheet.web

import io.ktor.http.ContentType
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.charsets.name
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.InputStream

fun InputStream.parseHtmlBody(url: String, charset: Charset?): Document {
    return Jsoup.parse(this, charset?.name ?: "utf-8", url)
}

fun ContentType?.isHtml(): Boolean {
    return this?.withoutParameters() == ContentType.Text.Html
}
