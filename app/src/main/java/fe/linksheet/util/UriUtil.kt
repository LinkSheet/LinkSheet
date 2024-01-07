package fe.linksheet.util

import android.content.Intent
import android.net.Uri
import android.util.Patterns
import fe.kotlin.extension.mapToSet

object UriUtil {
    private val webSchemeIntents: Set<Intent> = setOf("http", "https").mapToSet {
        Intent(Intent.ACTION_VIEW, Uri.fromParts(it, "", "")).addCategory(Intent.CATEGORY_BROWSABLE)
    }

    fun parseWebUri(url: String): Uri? {
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
}
