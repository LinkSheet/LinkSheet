package fe.linksheet

typealias RefreshHeader = Pair<Int, String>

object RedirectResolver {
    private val refreshHeaderRegex = Regex("(\\d+)(?:\\.\\d*)?[;,](?:URL=)?(.+)", RegexOption.IGNORE_CASE)

    fun parseRefreshHeader(refreshHeader: String): RefreshHeader? {
        fun unquoteHeader(value: String): String {
            if (value.length <= 2) return value

            val firstChar = value[0]
            val lastChar = value[value.length - 1]

            if ((firstChar == '"' && lastChar == '"') || (firstChar == '\'' && lastChar == '\'')) {
                return value.substring(1, value.length - 1)
            }

            return value
        }

        val (_, time, url) = refreshHeaderRegex.matchEntire(refreshHeader)?.groupValues ?: return null
        val intTime = time.toIntOrNull() ?: return null

        return intTime to unquoteHeader(url)
    }
}
