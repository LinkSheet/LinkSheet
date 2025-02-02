package fe.linksheet.extension.ktor

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType

fun HttpResponse.isHtml(): Boolean {
    return contentType() == ContentType.Text.Html
}

fun HttpResponse.urlString(): String {
    return request.url.toString()
}

