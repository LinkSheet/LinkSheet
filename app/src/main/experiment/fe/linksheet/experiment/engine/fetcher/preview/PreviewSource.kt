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
    ) : PreviewResult(url)

    class SimplePreviewResult(
        url: String,
        val htmlText: String,
        val title: String?,
        val favicon: String?
    ) : PreviewResult(url)

    class NonHtmlPage(url: String) : PreviewResult(url)
}
