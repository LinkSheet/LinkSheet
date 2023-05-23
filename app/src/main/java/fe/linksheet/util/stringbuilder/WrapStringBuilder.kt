package fe.linksheet.util.stringbuilder

import fe.linksheet.extension.wrapped

class WrapStringBuilder(
    private val wrapStart: String,
    private val wrapEnd: String,
) : BaseStringBuilder<StringBuilder, StringBuilder.() -> Unit> {

    override fun applyBuilder(stringBuilder: StringBuilder, block: StringBuilder.() -> Unit) {
        stringBuilder.append(wrapStart)
        stringBuilder.apply(block)
        stringBuilder.append(wrapEnd)
    }
}

fun buildWrappedString(
    wrapWith: String,
    builder: StringBuilder.() -> Unit
) = buildWrappedString(wrapWith, wrapWith, builder)

fun buildWrappedString(
    wrapStart: String,
    wrapEnd: String,
    builder: StringBuilder.() -> Unit
) = StringBuilder().wrapped(wrapStart, wrapEnd, builder).toString()
