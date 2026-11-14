package fe.linksheet.module.viewmodel

/** Keeps the original query entries byte-for-byte, including repeated names and empty values. */
data class LinkParameterSelection(
    val source: String,
    val parameters: List<String>,
    val selected: Set<Int>,
) {
    fun select(index: Int, keep: Boolean): LinkParameterSelection =
        if (index !in parameters.indices) this
        else copy(selected = if (keep) selected + index else selected - index)

    fun selectAll(keep: Boolean): LinkParameterSelection =
        copy(selected = if (keep) parameters.indices.toSet() else emptySet())

    val url: String
        get() {
            if (selected.size == parameters.size) return source
            val fragment = source.indexOf('#').let { if (it < 0) source.length else it }
            val query = source.indexOf('?')
            if (query < 0 || query >= fragment) return source
            val retained = parameters.filterIndexed { index, _ -> index in selected }
            return source.substring(0, query) +
                (if (retained.isEmpty()) "" else "?" + retained.joinToString("&")) +
                source.substring(fragment)
        }

    companion object {
        fun from(url: String): LinkParameterSelection {
            val fragment = url.indexOf('#').let { if (it < 0) url.length else it }
            val query = url.indexOf('?')
            val parameters = if (query >= 0 && query + 1 < fragment) {
                url.substring(query + 1, fragment).split('&')
            } else emptyList()
            return LinkParameterSelection(url, parameters, parameters.indices.toSet())
        }
    }
}
