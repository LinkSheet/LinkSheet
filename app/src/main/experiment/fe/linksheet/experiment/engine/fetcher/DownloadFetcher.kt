package fe.linksheet.experiment.engine.fetcher

import fe.linksheet.experiment.engine.FetchInput
import fe.linksheet.experiment.engine.FetchOutput
import fe.linksheet.experiment.engine.LinkFetcher
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.downloader.Downloader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadFetcher(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val downloader: Downloader,
    private val checkUrlMimeType: () -> Boolean = { false },
    private val requestTimeout: () -> Int = { 15 },
) : LinkFetcher {

    override suspend fun resolve(data: FetchInput): FetchOutput? = withContext(dispatcher) {
        if (checkUrlMimeType()) {
            val result = downloader.checkIsNonHtmlFileEnding(data.url)
//            if (result.isDownloadable()) return@withContext result
            FetchOutput(data.url)
        }

        downloader.isNonHtmlContentUri(data.url, requestTimeout())
        FetchOutput(data.url)
    }
}
