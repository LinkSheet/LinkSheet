package fe.linksheet.util.stringbuilder

import android.util.Log
import fe.linksheet.extension.forEachElementIndex
import fe.linksheet.extension.separated
import fe.linksheet.util.applyIfNotNull

class SeparatedStringBuilder(
    private val separator: String,
    private val items: MutableList<StringBuilder.() -> Unit> = mutableListOf()
) : BaseStringBuilder<SeparatedStringBuilder, (SeparatedStringBuilder.() -> Unit)?> {
    fun item(block: StringBuilder.() -> Unit) = items.add(block)

    fun <T> itemNotNull(predicate: T?, block: StringBuilder.() -> Unit) {
        if (predicate != null) item(block)
    }

    fun items(blocks: Array<out StringBuilder.() -> Unit>) = items.addAll(blocks)

    override fun applyBuilder(
        stringBuilder: StringBuilder,
        block: (SeparatedStringBuilder.() -> Unit)?
    ) {
        applyIfNotNull(block)

        items.forEachElementIndex { element, _, _, last ->
            stringBuilder.apply(element)
            if (!last) stringBuilder.append(separator)
        }
    }
}

fun buildSeparatedString(
    separator: String,
    stringBuilder: SeparatedStringBuilder.() -> Unit
) = StringBuilder().separated(separator, stringBuilder).toString()
