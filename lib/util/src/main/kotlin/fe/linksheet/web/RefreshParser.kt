package fe.linksheet.web

import org.jsoup.nodes.Document
import org.jsoup.select.QueryParser

typealias RefreshHeader = Pair<Int, String>

object RefreshParser {
    private val htmlRefreshMeta = QueryParser.parse("meta[http-equiv=refresh]")
    private val refreshContentRegex = """(\d+)(?:\.\d*)?[;,]\s*(?:URL=)?(.+)""".toRegex(RegexOption.IGNORE_CASE)

    fun parseRefreshHeader(refreshHeader: String): RefreshHeader? {
        return matchRefreshContent(refreshHeader)
    }

    fun parseHtml(document: Document): RefreshHeader? {
        val content = document.select(htmlRefreshMeta).firstOrNull()?.attr("content") ?: return null
        return matchRefreshContent(content)
    }

    internal fun matchRefreshContent(content: String): RefreshHeader? {
        val (_, time, url) = refreshContentRegex.matchEntire(content)?.groupValues ?: return null
        val intTime = time.toIntOrNull() ?: return null

        return intTime to unquoteContent(url)
    }

    private fun unquoteContent(value: String): String {
        if (value.length <= 2) return value

        val firstChar = value[0]
        val lastChar = value[value.length - 1]

        if ((firstChar == '"' && lastChar == '"') || (firstChar == '\'' && lastChar == '\'')) {
            return value.substring(1, value.length - 1)
        }

        return value
    }
}
