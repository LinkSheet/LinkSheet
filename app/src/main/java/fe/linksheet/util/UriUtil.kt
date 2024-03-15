package fe.linksheet.util

import android.content.Intent
import android.net.Uri
import android.util.Patterns
import fe.kotlin.extension.iterable.mapToSet

object UriUtil {
    private val protocols = setOf("http", "https")
    private val webSchemeIntents: Set<Intent> = protocols.mapToSet {
        Intent(Intent.ACTION_VIEW, Uri.fromParts(it, "", "")).addCategory(Intent.CATEGORY_BROWSABLE)
    }

    fun parseWebUri(url: String): Uri? {
        val isWeb = protocols.any { url.startsWith(it) }
        if (!isWeb) return null

        return if (Patterns.WEB_URL.matcher(url).matches()) {
            runCatching { Uri.parse(url) }.getOrNull()
        } else null
    }

    /**
     * Does not support "rtsp" and "ftp"
     */
    fun hasWebScheme(intent: Intent): Boolean {
        return webSchemeIntents.any { it.scheme == intent.scheme }
    }

    private const val HTTPS_PREFIX = "https://"
    private const val WWW_PREFIX = "www."

    fun declutter(uri: Uri): String {
        var str = uri.toString()
        if (str.startsWith(HTTPS_PREFIX)) {
            str = str.substring(HTTPS_PREFIX.length)
        }

        if (str.startsWith(WWW_PREFIX)) {
            str = str.substring(WWW_PREFIX.length)
        }

        return str
    }
}
