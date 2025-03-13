package fe.linksheet.experiment.engine.fetcher.preview

import fe.linksheet.experiment.engine.fetcher.FetchResult
import fe.std.result.IResult

interface PreviewSource {
    suspend fun fetch(urlString: String): IResult<PreviewResult>
    suspend fun parseHtml(htmlText: String, urlString: String)
}

sealed class PreviewResult(val url: String) : FetchResult {
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
