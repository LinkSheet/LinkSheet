package fe.linksheet.util.intent

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import fe.linksheet.util.web.UriUtil
import mozilla.components.support.utils.SafeIntent
import mozilla.components.support.utils.WebURLFinder
import kotlin.collections.set

object IntentParser {
    private val textExtras = setOf(
        Intent.EXTRA_TEXT,
        Intent.EXTRA_PROCESS_TEXT,
    )

    private val customExtras = setOf("url")

    private val queryExtras = arrayOf(
        SearchManager.QUERY,
        SearchManager.USER_QUERY
    )

    private fun readExtra(intent: SafeIntent, map: MutableMap<String, CharSequence>?, extra: String): Uri? {
        val value = intent.getCharSequenceExtra(extra)
        if (value == null) return null

        val uri = tryParseWebUriStrict(value)
        if (uri != null) return uri

        map?.set(extra, value)

        return null
    }

    fun parseSearchIntent(intent: SafeIntent): String? {
        for (extra in queryExtras) {
            val query = intent.getStringExtra(extra)
            if (query != null) return query
        }

        return null
    }

    fun parseViewAction(intent: SafeIntent): Uri? {
        if (intent.data != null) return intent.data

        for (extra in textExtras) {
            val uri = readExtra(intent, null, extra)
            if (uri != null) return uri
        }

        return null
    }

    fun parseProcessTextAction(intent: SafeIntent): Uri? {
        val processText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        if (processText != null) {
            return parseText(processText.toString())
        }

        return null
    }

    fun parseSendAction(
        intent: SafeIntent,
        allowCustomExtras: Boolean = true,
        tryParseAllExtras: Boolean = true,
        parseText: Boolean = true,
    ): Uri? {
        if (intent.data != null) return intent.data

        val extras = mutableMapOf<String, CharSequence>()

        for (extra in textExtras) {
            val uri = readExtra(intent, extras, extra)
            if (uri != null) return uri
        }

        if (allowCustomExtras) {
            for (customExtra in customExtras) {
                val uri = readExtra(intent, extras, customExtra)
                if (uri != null) return uri
            }
        }

        if (tryParseAllExtras && intent.extras != null) {
            val keys = intent.extras!!.keySet() - extras.keys
            for (key in keys) {
                val uri = readExtra(intent, extras, key)
                if (uri != null) return uri
            }
        }

        if (parseText && extras[Intent.EXTRA_TEXT] != null) {
            return parseText(extras[Intent.EXTRA_TEXT].toString())
        }

        return null
    }

    fun parseText(text: String): Uri? {
        var url = WebURLFinder(text).bestWebURL()
        if (url == null) return null

        if (!url.contains(":")) {
            // This URL does not have a scheme, default to http://
            url = "${UriUtil.HTTP}://$url"
        }

        return tryParseWebUriStrict(url)
    }

    fun tryParseWebUriStrict(text: CharSequence, allowInsecure: Boolean = true): Uri? {
        val str = text.toString()
        if (!UriUtil.isWebStrict(str, allowInsecure)) return null

        return runCatching { Uri.parse(str) }.getOrNull()
    }
}
