package fe.linksheet.experiment.engine.fetcher.preview

import fe.linksheet.experiment.engine.fetcher.FetchResult
import fe.std.result.IResult
import me.saket.unfurl.UnfurlResult
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

interface PreviewSource {
    suspend fun fetch(urlString: String): IResult<PreviewFetchResult>
    suspend fun parseHtml(htmlText: String, urlString: String): IResult<PreviewFetchResult>
}

sealed class PreviewFetchResult(val url: String) : FetchResult {
    class NoPreview(url: String) : PreviewFetchResult(url) {
        override fun toString(): String {
            return "NoPreview(url='$url')"
        }
    }

    class NonHtmlPage(url: String) : PreviewFetchResult(url) {
        override fun toString(): String {
            return "NonHtmlPage(url='$url')"
        }
    }
}

sealed class HtmlPreviewResult(url: String, val htmlText: String) : PreviewFetchResult(url) {
    class RichPreviewResult(
        url: String,
        htmlText: String,
        val title: String?,
        val description: String?,
        val favicon: String?,
        val thumbnail: String?,
    ) : HtmlPreviewResult(url, htmlText) {
        override fun toString(): String {
            return "RichPreviewResult(url='$url', htmlText='$htmlText', title=$title, description=$description, favicon=$favicon, thumbnail=$thumbnail)"
        }
    }

    class SimplePreviewResult(
        url: String,
        htmlText: String,
        val title: String?,
        val favicon: String?
    ) : HtmlPreviewResult(url, htmlText) {
        override fun toString(): String {
            return "SimplePreviewResult(url='$url', htmlText='$htmlText', title=$title, favicon=$favicon)"
        }
    }
}

fun PreviewFetchResult.toUnfurlResult(): UnfurlResult {
    return when(this) {
        is HtmlPreviewResult.RichPreviewResult -> UnfurlResult(
            url = url.toHttpUrl(),
            title = title,
            description = description,
            favicon = favicon?.toHttpUrlOrNull(),
            thumbnail = thumbnail?.toHttpUrlOrNull()
        )
        is HtmlPreviewResult.SimplePreviewResult -> UnfurlResult(
            url = url.toHttpUrl(),
            title = title,
            description = null,
            favicon = favicon?.toHttpUrlOrNull(),
            thumbnail = null
        )
        is PreviewFetchResult.NoPreview -> UnfurlResult(
            url = url.toHttpUrl(),
            title = null,
            description = null,
            favicon = null,
            thumbnail = null
        )
        is PreviewFetchResult.NonHtmlPage -> UnfurlResult(
            url = url.toHttpUrl(),
            title = null,
            description = null,
            favicon = null,
            thumbnail = null
        )
    }
}
