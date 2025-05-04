package fe.linksheet.experiment.engine.fetcher

import fe.linksheet.experiment.engine.fetcher.preview.PreviewFetchResult
import fe.std.uri.StdUrl

interface LinkFetcher<out Result : FetchResult> {
    val id: LinkFetcherId<Result>

    suspend fun fetch(url: StdUrl): Result?
}

interface FetchResult

sealed interface LinkFetcherId<out Result : FetchResult> {
    data object Download : LinkFetcherId<DownloadCheckFetchResult>
    data object Preview : LinkFetcherId<PreviewFetchResult>
}
