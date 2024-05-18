package fe.linksheet.util.spanformatter

class InterceptingAppendable(
    private val inputLen: Int,
    private val stringBuilder: StringBuilder = StringBuilder()
) : Appendable by stringBuilder {
    private var length: Int = 0

    private fun trackAppend(text: String): StringBuilder {
        length += text.length
        return stringBuilder.append(text)
    }

    private val formatChar = mutableMapOf<Int, Int>()
    private val adjustments = mutableMapOf<SpanPosition, SpanPosition>()

    override fun append(csq: CharSequence?): Appendable {
        if (csq == null) return this

//        println("append(csq=$csq)")

        val start = length
        val end = start + csq.length

        val diff = end - inputLen
        if (diff != 0) {
            val original = start to inputLen
            val replaced = start to end

            adjustments[original] = replaced
        }

        formatChar[end] = start
        return trackAppend(csq.toString())
    }

    override fun append(csq: CharSequence?, start: Int, end: Int): Appendable {
        if (csq == null) return this

//        println("append(csq=$csq, start=$start, end=$end)")

        if (start > 0) {
            // We might be skipping a format char, check its length
            // append(csq=foo)
            // append(csq=%sbar, start=2, end=5)
            val diff = length - start
            if (diff != 0) {
                // Replaced format char has different length than format char itself
                val formatStart = formatChar[length]
                if (formatStart != null) {
                    val original = formatStart to length - diff
                    val replaced = formatStart to length

                    adjustments[original] = replaced
                }
            }
        }

        return trackAppend(csq.substring(start, end))
    }

    override fun append(c: Char): Appendable {
        return stringBuilder.append(c)
    }

    override fun toString(): String {
        return stringBuilder.toString()
    }

    fun get(): Pair<String, Map<SpanPosition, SpanPosition>> {
        return toString() to adjustments
    }
}
