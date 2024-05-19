package fe.linksheet.util.spanformatter

import android.text.ParcelableSpan
import android.text.SpannableStringBuilder
import android.text.Spanned
import androidx.core.text.set
import androidx.core.text.toSpanned
import java.util.*

typealias SpanPosition = Pair<Int, Int>

object SpanFormatter {
    private fun getSpan(spanned: Spanned, tag: Any): SpanPosition {
        return spanned.getSpanStart(tag) to spanned.getSpanEnd(tag)
    }

    fun format(spanned: Spanned, vararg args: Any?): Spanned {
        if (args.isEmpty()) return spanned

        val text = spanned.toString()
        val interceptor = InterceptingAppendable(text.length)
        Formatter(interceptor).use { it.format(text, *args) }

        val (newString, adjustments) = interceptor.get()

        val builder = SpannableStringBuilder(newString)
        val allSpans = spanned.getSpans(0, text.length, ParcelableSpan::class.java)
            .associateBy { getSpan(spanned, it) }

        for ((pos, span) in allSpans) {
            val newPos = adjustments[pos] ?: pos
            builder[newPos.first, newPos.second] = span
        }

        return builder.toSpanned()
    }
}
