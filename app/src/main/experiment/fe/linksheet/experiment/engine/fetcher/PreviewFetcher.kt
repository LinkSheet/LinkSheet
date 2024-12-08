package fe.linksheet.experiment.engine.fetcher

import fe.linksheet.experiment.engine.FetchInput
import fe.linksheet.experiment.engine.FetchOutput
import fe.linksheet.experiment.engine.LinkFetcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.saket.unfurl.Unfurler

class PreviewFetcher(
    private val unfurler: Unfurler,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LinkFetcher {

    override suspend fun resolve(data: FetchInput): FetchOutput? = withContext(dispatcher) {
        unfurler.unfurl(data.url)
        FetchOutput(data.url)
    }
}
