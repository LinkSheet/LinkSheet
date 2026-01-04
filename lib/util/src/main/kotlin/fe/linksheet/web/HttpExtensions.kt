package fe.linksheet.web

import io.ktor.http.*

//fun InputStream.parseHtmlBody(url: String, charset: Charset?): Document {
//    return Jsoup.parse(this, charset?.name ?: "utf-8", url)
//}


fun ContentType?.isHtml(): Boolean {
    return this?.withoutParameters() == ContentType.Text.Html
}
