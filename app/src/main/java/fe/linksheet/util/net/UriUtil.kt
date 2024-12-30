package fe.linksheet.util.net

import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.webkit.URLUtil
import fe.kotlin.extension.iterable.mapToSet
import mozilla.components.support.ktx.util.URLStringUtils

object UriUtil {
    const val HTTP = "http"

    private val protocols = setOf(HTTP, "https")
    private val webSchemeIntents: Set<Intent> = protocols.mapToSet {
        Intent(Intent.ACTION_VIEW, Uri.fromParts(it, "", "")).addCategory(Intent.CATEGORY_BROWSABLE)
    }

    fun parseWebUriStrict(url: String): Uri? {
        if (!isWebStrict(url)) return null

        return if (Patterns.WEB_URL.matcher(url).matches()) {
            runCatching { Uri.parse(url) }.getOrNull()
        } else null
    }

    fun isWebStrict(url: String, allowInsecure: Boolean = true): Boolean {
        return (URLUtil.isHttpUrl(url) && allowInsecure) || URLUtil.isHttpsUrl(url)
    }

    /**
     * Does not support "rtsp" and "ftp"
     */
    @Deprecated(message = "Method needs refactoring")
    fun hasWebScheme(intent: Intent): Boolean {
        return webSchemeIntents.any { it.scheme == intent.scheme }
    }

    fun declutter(uri: Uri): String {
        val str = uri.toString()
        if (isWebStrict(str, allowInsecure = false)) {
            return URLStringUtils.toDisplayUrl(str).toString()
        }
        return str
    }
}
