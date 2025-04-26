package fe.linksheet.experiment.engine.fetcher

import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.downloader.Downloader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DownloadLinkFetcher(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val downloader: Downloader,
    private val checkUrlMimeType: () -> Boolean = { false },
    private val requestTimeout: () -> Int = { 15 },
) : LinkFetcher<DownloadCheckFetchResult> {

    override suspend fun fetch(data: FetchInput) = withContext(ioDispatcher) {
        if (checkUrlMimeType()) {
            val result = downloader.checkIsNonHtmlFileEnding(data.url)
            if (result.isDownloadable()) {
                return@withContext result.toFetchResult()
            }
        }

        val result = downloader.isNonHtmlContentUri(data.url, requestTimeout())
        result.toFetchResult()
    }
}

sealed interface DownloadCheckFetchResult : FetchResult {
    data class Downloadable(private val fileName: String) : DownloadCheckFetchResult
    data object MimeTypeDetectionFailed : DownloadCheckFetchResult
    data object NonDownloadable : DownloadCheckFetchResult
}

fun DownloadCheckResult.toFetchResult(): DownloadCheckFetchResult {
    return when (this) {
        is DownloadCheckResult.Downloadable -> DownloadCheckFetchResult.Downloadable(toFileName())
        DownloadCheckResult.MimeTypeDetectionFailed -> DownloadCheckFetchResult.MimeTypeDetectionFailed
        DownloadCheckResult.NonDownloadable -> DownloadCheckFetchResult.NonDownloadable
    }
}
