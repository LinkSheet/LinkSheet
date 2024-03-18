package fe.linksheet.util

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import mozilla.components.support.utils.SafeIntent
import mozilla.components.support.utils.WebURLFinder

object IntentParser {
    private val customExtras = setOf("url")

    fun parseSearchIntent(intent: SafeIntent): String? {
        val query = intent.getStringExtra(SearchManager.QUERY)
        if (query != null) return query

        return intent.getStringExtra(SearchManager.USER_QUERY)
    }

    fun parseViewAction(intent: SafeIntent): Uri? {
        if (intent.data != null) return intent.data

        val text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)
        if (text != null) tryParse(text)?.let { return it }

        val processText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        if (processText != null) tryParse(processText)?.let { return it }

        return null
    }

    fun parseSendAction(
        intent: SafeIntent,
        allowCustomExtras: Boolean = false,
        tryParseAllExtras: Boolean = false,
        parseText: Boolean = false,
    ): Uri? {
        if (intent.data != null) return intent.data

        val extras = mutableMapOf<String, CharSequence>()

        val text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)
        if (text != null) {
            tryParse(text)?.let { return it }
            extras[Intent.EXTRA_TEXT] = text
        }

        val processText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        if (processText != null) {
            tryParse(processText)?.let { return it }
            extras[Intent.EXTRA_PROCESS_TEXT] = processText
        }

        if (allowCustomExtras) {
            for (customExtra in customExtras) {
                val value = intent.getCharSequenceExtra(customExtra)
                if (value != null) {
                    tryParse(value)?.let { return it }
                    extras[customExtra] = value
                }
            }
        }

        if (tryParseAllExtras && intent.extras != null) {
            val keys = intent.extras!!.keySet() - extras.keys
            for (key in keys) {
                val value = intent.getCharSequenceExtra(key)
                if (value != null) {
                    tryParse(value)?.let { return it }
                    extras[key] = value
                }
            }
        }

        if (parseText && text != null) {
            var url = WebURLFinder(text.toString()).bestWebURL()
            if (url != null) {
                if(!url.contains(":")) {
                    // This URL does not have a scheme, default to http://
                    url = "${UriUtil.HTTP}://$url"
                }

                tryParse(url)?.let { return it }
            }
        }

        return null
    }

    fun tryParse(text: CharSequence): Uri? {
        val str = text.toString()
        if (UriUtil.isWebStrict(str)) {
            return kotlin.runCatching { Uri.parse(str) }.getOrNull()
        }

        return null
    }
}
