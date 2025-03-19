package fe.linksheet.experiment.engine.fetcher

interface LinkFetcher<Result : FetchResult> {
    suspend fun fetch(data: FetchInput): Result?
}

interface FetchResult {

}

data class FetchInput(val url: String)
