package app.linksheet.feature.downloader

import app.linksheet.feature.downloader.DownloadCheckResult.Downloadable
import fe.httpkt.util.Extension
import fe.linksheet.util.mime.KnownMimeTypes
import fe.linksheet.util.mime.MimeType
import fe.std.result.isFailure
import fe.std.result.tryCatch
import fe.std.uri.StdUrl
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.dsl.module

val DownloaderModule = module {
    single<Downloader> {
        Downloader(client = get())
    }
}

sealed interface DownloadCheckResult {
    class Downloadable(val fileName: String, val extension: String?) : DownloadCheckResult {
        fun toFileName() = "$fileName.$extension"
    }

    data object NonDownloadable : DownloadCheckResult
    data object MimeTypeDetectionFailed : DownloadCheckResult
}

fun DownloadCheckResult.isDownloadable() = this is Downloadable

class Downloader(
    private val client: HttpClient
) {

    companion object {
        private val mimeTypeToExtension = KnownMimeTypes.mimeTypeToExtensions
        private val extensionToMimeType = KnownMimeTypes.extensionToMimeType
    }

    fun checkIsNonHtmlFileEnding(url: StdUrl): DownloadCheckResult {
        val (fileName, ext) = Extension.getFileNameFromUrl(url.url)
        if (fileName == null || ext == null) return DownloadCheckResult.MimeTypeDetectionFailed

        val mimeType = extensionToMimeType[ext] ?: return DownloadCheckResult.MimeTypeDetectionFailed
        return checkMimeType(mimeType, fileName, ext)
    }

    suspend fun isNonHtmlContentUri(url: StdUrl): DownloadCheckResult {
        val result = tryCatch {
            client.get(urlString = url.toString())
        }
        if (result.isFailure()) {
            return DownloadCheckResult.NonDownloadable
        }
        if (!result.value.status.isSuccess()) return DownloadCheckResult.NonDownloadable

        val contentType =
            result.value.contentType()?.withoutParameters()?.toString() ?: return DownloadCheckResult.NonDownloadable
        val (fileName, _) = Extension.getFileNameFromUrl(url.url)

        return checkMimeType(contentType, fileName ?: url.toString(), mimeTypeToExtension[contentType]?.firstOrNull())
    }

    private fun checkMimeType(mimeType: String, fileName: String, extension: String?): DownloadCheckResult {
        return if (mimeType != MimeType.TEXT_HTML) DownloadCheckResult.Downloadable(fileName, extension)
        else DownloadCheckResult.NonDownloadable
    }
}
