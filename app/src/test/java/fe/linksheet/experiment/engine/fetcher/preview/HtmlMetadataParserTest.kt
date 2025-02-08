package fe.linksheet.experiment.engine.fetcher.preview

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.tableOf
import fe.linksheet.UnitTest
import fe.linksheet.extension.ktor.parseHtmlBody
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.coroutines.test.runTest
import me.saket.unfurl.UnfurlResult
import me.saket.unfurl.Unfurler
import me.saket.unfurl.defaultOkHttpClient
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.nodes.Element
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class HtmlMetadataParserTest : UnitTest {

    private val parser = HtmlMetadataParser()

    @Test
    fun `test size parsing`() {
        tableOf("size", "expected")
            .row<String, Int?>("", null)
            .row("1", null)
            .row("1x", null)
            .row("x1", null)
            .row("axb", null)
            .row("1x0", 1)
            .forAll { size, expected ->
                assertThat(parser.parseSize(size)).isEqualTo(expected)
            }
    }

    @Test
    fun `test largest icon finder`() {
        fun Element.createElement(href: String, sizes: String? = null): Element {
            return appendElement("link")
                .attr("rel", "icon")
                .attr("href", href).apply {
                    if (sizes != null) attr("sizes", sizes)
                }
        }

        tableOf("linkElements", "expected")
            .row(
                Element("head").apply { createElement("https://single.com") },
                "https://single.com"
            )
            .row(
                Element("head").apply {
                    createElement("https://smaller.com", "100x100")
                    createElement("https://larger.com", "200x100")
                },
                "https://larger.com"
            )
            .forAll { elements, expected ->
                assertThat(parser.findLargestIconOrNull(elements)).isEqualTo(expected)
            }
    }

    @Test
    fun `test parse unfurling`() = runTest {
        val url = "https://www.getproactiv.ca/pdp?productcode=842944100695"
        val httpClient = Unfurler.defaultOkHttpClient()

        val client = HttpClient(OkHttp) {
            engine {
                preconfigured = httpClient
            }
        }
        val response = client.get(urlString = url)
        val document = response.parseHtmlBody()
        val result = HtmlMetadataParser().parse(document, document.html())

        assertThat(result).isEqualTo(
            UnfurlResult(
                url = "https://www.getproactiv.ca/proactiv-solution-repairing-treatment/p/842944100695?productcode=842944100695".toHttpUrl(),
                title = "Proactiv Solution® Repairing Treatment | Proactiv® Products",
                description = "Our Repairing Treatment is a leave-on treatment formulated with prescription-grade benzoyl peroxide designed to penetrate pores to kill acne-causing bacteria.",
                favicon = "https://www.getproactiv.ca/favicon.ico".toHttpUrl(),
                thumbnail = "https://cdn-tp3.mozu.com/30113-50629/cms/50629/files/f050a010-0420-4a53-b898-d4c08db77eb9".toHttpUrl(),
            )
        )
    }
}
