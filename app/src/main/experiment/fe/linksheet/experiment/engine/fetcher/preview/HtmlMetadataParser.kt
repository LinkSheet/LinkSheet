package fe.linksheet.experiment.engine.fetcher.preview

import fe.std.uri.Url
import fe.std.uri.extension.buildUrl
import fe.std.uri.toUrlOrThrow
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Evaluator
import org.jsoup.select.QueryParser

// Adapted from https://github.com/saket/unfurl
class HtmlMetadataParser {
    companion object {
        private val titleMetaTags = arrayOf(
            lazy { makeMetaTagQuery("twitter:title") },
            lazy { makeMetaTagQuery("og:title") }
        )

        private val descriptionMetaTags = arrayOf(
            lazy { makeMetaTagQuery("twitter:description") },
            lazy { makeMetaTagQuery("og:description") },
            lazy { makeMetaTagQuery("description") }
        )

        private val imageMetaTags = arrayOf(
            lazy { makeMetaTagQuery("twitter:image") },
            lazy { makeMetaTagQuery("og:image") },
            lazy { makeMetaTagQuery("twitter:image:src") },
            lazy { makeMetaTagQuery("og:image:secure_url") }
        )

        private val linkRelTags = arrayOf(
            lazy { makeLinkRelQuery("apple-touch-icon") },
            lazy { makeLinkRelQuery("apple-touch-icon-precomposed") },
            lazy { makeLinkRelQuery("shortcut icon") },
            lazy { makeLinkRelQuery("icon") }
        )

        private fun makeLinkRelQuery(rel: String): Evaluator {
            return QueryParser.parse("link[rel=$rel]")
        }

        private fun makeMetaTagQuery(attr: String): Evaluator {
            return QueryParser.parse("meta[name=$attr],meta[property=$attr]")
        }
    }

    fun parse(
        htmlText: String,
        baseUri: String,
    ): PreviewFetchResult {
        val document = Jsoup.parse(htmlText, baseUri)
        return parse(document, htmlText)
    }

    fun parse(document: Document, htmlText: String): PreviewFetchResult {
        val url = document.baseUri().toUrlOrThrow()
        val urlStr = url.toString()

        val richTitle = parseTitle(document)
        val richDescription = parseDescription(document)
        val richFavicon = parseFaviconUrl(document)
        val richThumbnail = parseThumbnailUrl(document)

        val hasAnyRich = richTitle != null || richDescription != null || richFavicon != null || richThumbnail != null
        val documentTitle = document.title().ifBlank { null }

        if (documentTitle == null && !hasAnyRich) {
            return PreviewFetchResult.NoPreview(url = urlStr)
        }

        val title = richTitle ?: documentTitle
        if (!hasAnyRich) {
            return HtmlPreviewResult.SimplePreviewResult(
                url = urlStr,
                htmlText = htmlText,
                title = title,
                favicon = fallbackFaviconUrl(url),
            )
        }

        return HtmlPreviewResult.RichPreviewResult(
            url = urlStr,
            htmlText = htmlText,
            title = title,
            description = parseDescription(document),
            favicon = parseFaviconUrl(document) ?: fallbackFaviconUrl(url),
            thumbnail = parseThumbnailUrl(document)
        )
    }

    private fun parseTitle(document: Document): String? {
        return getMetaTagContent(document, titleMetaTags)
    }

    private fun parseDescription(document: Document): String? {
        return getMetaTagContent(document, descriptionMetaTags)
    }

    private fun parseThumbnailUrl(document: Document): String? {
        // Twitter's image tag is preferred over facebook's
        // because websites seem to give better images for twitter.
        return getMetaTagContent(document, imageMetaTags, isUrl = true)
    }

    private fun parseFaviconUrl(document: Document): String? {
        return findLargestIconOrNull(document.head(), linkRelTags)
    }

    private fun fallbackFaviconUrl(url: Url): String {
        return buildUrl {
            scheme = url.scheme
            host = url.host
            setPath("/favicon.ico")
        }.toString()
    }

    internal fun getMetaTagContent(element: Element, tags: Array<Lazy<Evaluator>>, isUrl: Boolean = false): String? {
        return tags.firstNotNullOfOrNull { query ->
            element.select(query.value)
                .attr(if (isUrl) "abs:content" else "content")
                .ifBlank { null }
        }
    }

    internal fun findLargestIconOrNull(element: Element, tags: Array<Lazy<Evaluator>> = linkRelTags): String? {
        return tags.firstNotNullOfOrNull { query ->
            element
                .select(query.value)
                .associate { it.attr("abs:href") to it.attr("sizes") }
                .let { parseSizeAndFindLargest(it) }
        }
    }

    internal fun parseSize(sizes: String): Int? {
        return sizes
            .takeIf { it.length >= 3 }
            ?.indexOf("x")
            ?.takeIf { it != -1 }
            ?.let { sizes.substring(0, it).toIntOrNull() }
    }

    internal fun parseSizeAndFindLargest(map: Map<String, String>): String? {
        val largestTag = map
            .mapValues { (_, sizes) -> parseSize(sizes) }
            .maxByOrNull { (_, sizes) -> sizes ?: 0 }

        return largestTag?.key
    }
}
