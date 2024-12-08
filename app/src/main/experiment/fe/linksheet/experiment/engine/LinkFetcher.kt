package fe.linksheet.experiment.engine

interface LinkFetcher {
    suspend fun resolve(data: FetchInput): FetchOutput?
}

data class FetchInput(val url: String)

data class FetchOutput(val url: String)
