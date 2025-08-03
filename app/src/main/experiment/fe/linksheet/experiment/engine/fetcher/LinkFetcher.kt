package fe.linksheet.experiment.engine.fetcher

import fe.std.uri.StdUrl

interface LinkFetcher<out Result : FetchResult> {
    val enabled: () -> Boolean
    val id: ContextResultId<Result>

    suspend fun fetch(url: StdUrl): Result?
}

abstract class AbstractLinkFetcher<out Result : FetchResult>(
    override val id: ContextResultId<Result>
) : LinkFetcher<Result> {

    override fun toString(): String {
        return id.toString()
    }
}

interface FetchResult : ContextResult
