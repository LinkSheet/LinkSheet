package fe.linksheet.experiment.engine.fetcher

import fe.std.uri.StdUrl

interface LinkFetcher<out Result : FetchResult> {
    val id: ContextResultId<Result>

    suspend fun fetch(url: StdUrl): Result?
}

interface FetchResult : ContextResult
