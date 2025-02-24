package fe.linksheet.util.intent.parser

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import fe.linksheet.util.web.UriUtil
import fe.std.result.Failure
import fe.std.result.IResult
import fe.std.result.isSuccess
import fe.std.result.mapFailure
import fe.std.result.success
import fe.std.result.tryCatch
import mozilla.components.support.utils.SafeIntent
import mozilla.components.support.utils.WebURLFinder

object IntentParser : InternalIntentParser()

open class InternalIntentParser internal constructor(
    private val textExtras: Array<String> = arrayOf(Intent.EXTRA_TEXT, Intent.EXTRA_PROCESS_TEXT),
    private val queryExtras: Array<String> = arrayOf(SearchManager.QUERY, SearchManager.USER_QUERY),
    private val customExtras: Array<String> = arrayOf("url")
) {
    fun parseSearchIntent(intent: SafeIntent): String? {
        for (extra in queryExtras) {
            val query = intent.getStringExtra(extra)
            if (query != null) return query
        }

        return null
    }

    fun getUriFromIntent(intent: SafeIntent): IResult<Uri> {
        return when (val action = intent.action) {
            Intent.ACTION_SEND -> parseSendAction(intent)
            Intent.ACTION_VIEW -> parseViewAction(intent)
            Intent.ACTION_PROCESS_TEXT -> parseProcessTextAction(intent)
            else -> Failure(UnsupportedIntentActionException(action))
        }
    }

    internal fun parseViewAction(intent: SafeIntent): IResult<Uri> {
        if (intent.data != null) return intent.data!!.success

        for (extra in textExtras) {
            val uri = readExtra(intent, null, extra)
            if (uri != null) return uri
        }

        return NoUriFoundFailure
    }

    private fun parseProcessTextAction(intent: SafeIntent): IResult<Uri> {
        val processText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        if (processText != null) {
            return parseText(processText.toString())
        }

        return Failure(NoSuchExtraException(Intent.EXTRA_PROCESS_TEXT))
    }

    private fun parseSendAction(
        intent: SafeIntent,
        allowCustomExtras: Boolean = true,
        tryParseAllExtras: Boolean = true,
        parseText: Boolean = true,
    ): IResult<Uri> {
        if (intent.data != null) return intent.data!!.success

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

        return NoUriFoundFailure
    }

    internal fun parseText(text: String): IResult<Uri> {
        var url = WebURLFinder(text).bestWebURL()
        if (url == null) return Failure(NoUriFoundRecoverableException(text))

        if (!url.contains(":")) {
            // This URL does not have a scheme, default to http://
            url = "${UriUtil.HTTP}://$url"
        }

        return tryParseWebUriStrict(url)
    }

    private fun tryParseWebUriStrict(text: CharSequence, allowInsecure: Boolean = true): IResult<Uri> {
        val str = text.toString()
        if (!UriUtil.isWebStrict(str, allowInsecure)) return Failure(NoUriFoundRecoverableException(str))

        return tryCatch { Uri.parse(str) }.mapFailure {
            NoUriFoundRecoverableException(str, it)
        }
    }

    private fun readExtra(intent: SafeIntent, map: MutableMap<String, CharSequence>?, extra: String): IResult<Uri>? {
        val value = intent.getCharSequenceExtra(extra)
        if (value == null) return Failure(NoSuchExtraException(extra))

        val result = tryParseWebUriStrict(value)
        if (result.isSuccess()) return result

        map?.set(extra, value)
        return null
    }
}

private val NoUriFoundFailure = Failure<Uri>(NoUriFoundException)

sealed class UriException : Exception()


data object NoUriFoundException : UriException() {
    private fun readResolve(): Any = NoUriFoundException
}

data class NoUriFoundRecoverableException(val recoverable: String, val wrappedEx: Throwable? = null) : UriException() {

}

data class NoSuchExtraException(val name: String) : UriException()

data class UnsupportedIntentActionException(val action: String?) : UriException()
