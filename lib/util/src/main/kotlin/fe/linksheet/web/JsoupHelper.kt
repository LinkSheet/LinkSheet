package fe.linksheet.web

import io.ktor.utils.io.charsets.*
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import org.jsoup.parser.StreamParser
import org.jsoup.select.Evaluator
import org.jsoup.select.QueryParser
import java.io.InputStream

object JsoupHelper {
    private val headSelector = QueryParser.parse("head")

    fun parseHeadAsStream(stream: InputStream, url: String, charset: Charset?, evaluator: Evaluator = headSelector): Document {
        val parser = StreamParser(Parser.htmlParser())
        parser.parse(stream.bufferedReader(charset ?: Charsets.UTF_8), url)
        return parser.use {
            it.selectFirst(evaluator)
            it.document()
        }
    }
}

