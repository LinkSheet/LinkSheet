package fe.linksheet.extension.okhttp

import okhttp3.MediaType

object CommonMediaType {
    val HTML_MIME_TYPE = "text" to "html"
}

fun MediaType.checkMime(mimeType: Pair<String, String>): Boolean {
    return type == mimeType.first && subtype == mimeType.second
}

fun MediaType.isHtml(): Boolean {
    return checkMime(CommonMediaType.HTML_MIME_TYPE)
}
