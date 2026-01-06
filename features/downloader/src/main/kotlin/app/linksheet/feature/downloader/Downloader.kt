package app.linksheet.feature.downloader

import app.linksheet.feature.downloader.DownloadCheckResult.Downloadable
import fe.linksheet.extension.ktor.contentDisposition
import fe.linksheet.util.mime.MimeType
import fe.linksheet.web.ContentDispositionHelper
import fe.mimetype.KnownMimeTypes
import fe.std.result.isFailure
import fe.std.result.tryCatch
import fe.std.uri.StdUrl
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.koin.dsl.module

val DownloaderModule = module {
    single<Downloader> {
        Downloader(client = get())
    }
}

sealed interface DownloadCheckResult {
    //    class Downloadable(val fileName: String, val extension: String?) : DownloadCheckResult {
//        fun toFileName() = "$fileName.$extension"
//    }
    class Downloadable(val fileName: String) : DownloadCheckResult

    data object NonDownloadable : DownloadCheckResult
    data object MimeTypeDetectionFailed : DownloadCheckResult
}

fun DownloadCheckResult.isDownloadable() = this is Downloadable

class Downloader(
    private val client: HttpClient
) {
    fun checkIsNonHtmlFileEnding(url: StdUrl): DownloadCheckResult {
        val lastSegment = url.pathSegments.lastOrNull() ?: return DownloadCheckResult.MimeTypeDetectionFailed
        val (fileNameOnly, extension) = getFileNameAndExtension(lastSegment)
        if (extension == null) return DownloadCheckResult.MimeTypeDetectionFailed

        val mimeType = KnownMimeTypes.getMimeTypeForExtensionOrNull(extension)
            ?: return DownloadCheckResult.MimeTypeDetectionFailed
        if (mimeType == MimeType.TEXT_HTML) return DownloadCheckResult.NonDownloadable
        return Downloadable("$fileNameOnly.$extension")
    }

    suspend fun isNonHtmlContentUri(url: StdUrl): DownloadCheckResult {
        val result = tryCatch { client.get(urlString = url.toString()) }

        if (result.isFailure()) return DownloadCheckResult.NonDownloadable
        val value = result.value
        if (!value.status.isSuccess()) return DownloadCheckResult.NonDownloadable

        return value.findFileName() ?: DownloadCheckResult.NonDownloadable
    }

    private fun getFileNameAndExtension(fileName: String): Pair<String, String?> {
        val foundExtension = KnownMimeTypes.findKnownExtensions(fileName).firstOrNull()
        if (foundExtension != null) {
            return foundExtension.fileNameOnly to foundExtension.extension
        }

        return fileName to null
    }

    internal fun HttpResponse.findFileName(): Downloadable? {
        val contentDispositionFileName = contentDisposition()?.let { ContentDispositionHelper.getFileName(it) }
        if (contentDispositionFileName != null) {
            return Downloadable(contentDispositionFileName)
        }

        val contentType = contentType()?.withoutParameters()?.toString()
        if (contentType == MimeType.TEXT_HTML) return null

        val contentTypeExtension = contentType?.let {
            KnownMimeTypes.getExtensionsForMimeTypeOrNull(it)?.firstOrNull()
        }

        val lastSegment = request.url.segments.lastOrNull() ?: return null
        val (fileNameOnly, segmentExtension) = getFileNameAndExtension(lastSegment)
        if (segmentExtension != null) {
            if (contentTypeExtension != null && contentTypeExtension != segmentExtension) {
                return Downloadable("$fileNameOnly.$contentTypeExtension")
            }

            return Downloadable("$fileNameOnly.$segmentExtension")
        }

        if (contentTypeExtension != null) {
            return Downloadable("$lastSegment.$contentTypeExtension")
        }

        return Downloadable(lastSegment)
    }
}
