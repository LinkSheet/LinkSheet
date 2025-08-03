package fe.linksheet.experiment.engine.fetcher

import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.downloader.Downloader
import fe.std.uri.StdUrl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadLinkFetcher(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val downloader: Downloader,
    private val checkUrlMimeType: () -> Boolean = { false },
    private val requestTimeout: () -> Int = { 15 },
) : AbstractLinkFetcher<DownloadCheckFetchResult>(ContextResultId.Download) {

    override suspend fun fetch(url: StdUrl) = withContext(ioDispatcher) {
        if (checkUrlMimeType()) {
            val result = downloader.checkIsNonHtmlFileEnding(url)
            if (result.isDownloadable()) {
                return@withContext result.toFetchResult()
            }
        }

        val result = downloader.isNonHtmlContentUri(url, requestTimeout())
        result.toFetchResult()
    }
}

sealed interface DownloadCheckFetchResult : FetchResult {
    data class Downloadable(
        val fileName: String,
        val extension: String?,
        val toFileName: String
    ) : DownloadCheckFetchResult

    data object MimeTypeDetectionFailed : DownloadCheckFetchResult
    data object NonDownloadable : DownloadCheckFetchResult
}

fun DownloadCheckResult.toFetchResult(): DownloadCheckFetchResult {
    return when (this) {
        is DownloadCheckResult.Downloadable -> DownloadCheckFetchResult.Downloadable(fileName, extension, toFileName())
        DownloadCheckResult.MimeTypeDetectionFailed -> DownloadCheckFetchResult.MimeTypeDetectionFailed
        DownloadCheckResult.NonDownloadable -> DownloadCheckFetchResult.NonDownloadable
    }
}

fun DownloadCheckFetchResult.toFetchResult(): DownloadCheckResult {
    return when (this) {
        is DownloadCheckFetchResult.Downloadable -> DownloadCheckResult.Downloadable(fileName, extension)
        DownloadCheckFetchResult.MimeTypeDetectionFailed -> DownloadCheckResult.MimeTypeDetectionFailed
        DownloadCheckFetchResult.NonDownloadable -> DownloadCheckResult.NonDownloadable
    }
}
