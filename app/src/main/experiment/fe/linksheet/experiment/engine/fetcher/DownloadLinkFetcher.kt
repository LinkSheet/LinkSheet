package fe.linksheet.experiment.engine.fetcher

import fe.linksheet.module.downloader.Downloader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadLinkFetcher(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val downloader: Downloader,
    private val checkUrlMimeType: () -> Boolean = { false },
    private val requestTimeout: () -> Int = { 15 },
) : LinkFetcher {

    override suspend fun resolve(data: FetchInput): FetchOutput? = withContext(ioDispatcher) {
        if (checkUrlMimeType()) {
            val result = downloader.checkIsNonHtmlFileEnding(data.url)
//            if (result.isDownloadable()) return@withContext result
            FetchOutput(data.url)
        }

        downloader.isNonHtmlContentUri(data.url, requestTimeout())
        FetchOutput(data.url)
    }
}
