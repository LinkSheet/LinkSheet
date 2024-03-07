package fe.linksheet.module.test

class ListStringBuilder : Appendable {
    private val segments = mutableListOf<String>()
    private val stringBuilder = StringBuilder()

    override fun append(csq: CharSequence?): ListStringBuilder {
        stringBuilder.append(csq)
        return this
    }

    override fun append(csq: CharSequence?, start: Int, end: Int): ListStringBuilder {
        stringBuilder.append(csq, start, end)
        return this
    }

    override fun append(c: Char): ListStringBuilder {
        stringBuilder.append(c)
        return this
    }

    fun parameter() {
        val segment = stringBuilder.toString()
        segments.add(segment)

        stringBuilder.setLength(0)
    }
}
