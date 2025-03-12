package fe.linksheet.experiment.engine.fetcher.preview

import fe.std.result.IResult

interface PreviewSource {
    suspend fun fetch(urlString: String): IResult<PreviewResult>
}

sealed class PreviewResult(val url: String) {
    class RichPreviewResult(
        url: String,
        val htmlText: String,
        val title: String?,
        val description: String?,
        val favicon: String?,
        val thumbnail: String?,
    ) : PreviewResult(url) {
        override fun toString(): String {
            return "RichPreviewResult(url='$url', htmlText='$htmlText', title=$title, description=$description, favicon=$favicon, thumbnail=$thumbnail)"
        }
    }

    class SimplePreviewResult(
        url: String,
        val htmlText: String,
        val title: String?,
        val favicon: String?
    ) : PreviewResult(url) {
        override fun toString(): String {
            return "SimplePreviewResult(url='$url', htmlText='$htmlText', title=$title, favicon=$favicon)"
        }
    }

    class NonHtmlPage(url: String) : PreviewResult(url) {
        override fun toString(): String {
            return "NonHtmlPage(url='$url')"
        }
    }
}
