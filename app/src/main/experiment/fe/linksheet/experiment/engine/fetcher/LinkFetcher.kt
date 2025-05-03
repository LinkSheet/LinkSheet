package fe.linksheet.experiment.engine.fetcher

import fe.std.uri.StdUrl

interface LinkFetcher<Result : FetchResult> {
    val id: LinkFetcherId

    suspend fun fetch(url: StdUrl): Result?
}

interface FetchResult

enum class LinkFetcherId {
    Download,
    Preview
}
