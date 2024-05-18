package fe.linksheet.extension.android

import android.text.Spanned
import fe.linksheet.util.spanformatter.SpanFormatter

fun Spanned.format(vararg args: Any?): Spanned {
    return SpanFormatter.format(this, args)
}
