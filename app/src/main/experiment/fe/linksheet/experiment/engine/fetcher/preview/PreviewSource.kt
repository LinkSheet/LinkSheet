package fe.linksheet.experiment.engine.fetcher.preview

import fe.linksheet.experiment.engine.fetcher.FetchResult
import fe.std.result.IResult
import me.saket.unfurl.UnfurlResult
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

interface PreviewSource {
    suspend fun fetch(urlString: String): IResult<PreviewResult>
    suspend fun parseHtml(htmlText: String, urlString: String): IResult<PreviewResult>
}

sealed class PreviewResult(val url: String) : FetchResult {
    class NoPreview(url: String) : PreviewResult(url) {
        override fun toString(): String {
            return "NoPreview(url='$url')"
        }
    }

    class NonHtmlPage(url: String) : PreviewResult(url) {
        override fun toString(): String {
            return "NonHtmlPage(url='$url')"
        }
    }
}

sealed class HtmlPreviewResult(url: String, val htmlText: String) : PreviewResult(url) {
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

fun PreviewResult.toUnfurlResult(): UnfurlResult {
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
        is PreviewResult.NoPreview -> UnfurlResult(
            url = url.toHttpUrl(),
            title = null,
            description = null,
            favicon = null,
            thumbnail = null
        )
        is PreviewResult.NonHtmlPage -> UnfurlResult(
            url = url.toHttpUrl(),
            title = null,
            description = null,
            favicon = null,
            thumbnail = null
        )
    }
}
