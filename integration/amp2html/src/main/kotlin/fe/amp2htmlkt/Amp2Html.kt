package fe.amp2htmlkt

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import java.io.InputStream


public object Amp2Html {
    public fun getNonAmpLink(
        html: String,
        baseUri: String,
        referrer: String? = null,
    ): String? = parseHtml(Jsoup.parse(html, baseUri), baseUri, referrer)

    public fun getNonAmpLink(
        stream: InputStream,
        baseUri: String,
        referrer: String? = null,
    ): String? = parseHtml(Jsoup.parse(stream, "utf-8", baseUri), baseUri, referrer)

    private fun parseHtml(document: Document, location: String, referrer: String?): String? {
        val head = document.head()

        val ampHref = head.select("link[rel='amphtml'][href]").firstOrNull()?.href()
        val canonicalHref = head.select("link[rel='canonical'][href]").firstOrNull()?.href()
        val ampHtml = document.select("html[amp],html[⚡]").firstOrNull()

        if (ampHref == canonicalHref) {
            return null
        }

        if ((ampHtml != null || ampHref == location) && (canonicalHref != location && canonicalHref != referrer)) {
            return canonicalHref
        }

        return null
    }

    private fun Node.href() = attr("href")
}
