package fe.linksheet.extension.java

import fe.httpkt.ext.findHeader
import fe.linksheet.util.mime.MimeType
import java.net.HttpURLConnection


const val CONTENT_TYPE_HEADER = "Content-Type"

fun HttpURLConnection.normalizedContentType(): String? {
    return findHeader(CONTENT_TYPE_HEADER)?.values
        ?.firstOrNull()
        ?.toString()
        ?.substringBefore(";")
        ?.lowercase()
}

fun HttpURLConnection.isHtml(): Boolean {
    return normalizedContentType() == MimeType.TEXT_HTML
}
