package fe.linksheet.module.downloader

import fe.httpkt.ext.findHeader
import fe.httpkt.util.Extension
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.module.log.impl.Logger
import fe.linksheet.module.log.impl.hasher.FileExtensionProcessor
import fe.linksheet.module.log.impl.hasher.FileNameProcessor
import fe.linksheet.module.log.impl.hasher.LogDumpable
import fe.linksheet.module.log.impl.hasher.LogHasher
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.mimetypekt.MimeTypes
import fe.stringbuilder.util.commaSeparated
import fe.stringbuilder.util.curlyWrapped
import org.koin.dsl.module
import java.io.IOException
import java.net.URL


val downloaderModule = module {
    single<Downloader> {
        Downloader(get(), createLogger<Downloader>())
    }
}

class Downloader(private val cachedRequest: CachedRequest, private val logger: Logger) {

    companion object {
        private const val CONTENT_TYPE_HEADER = "Content-Type"
        private const val HTML_MIME_TYPE = "text/html"

        private val mimeTypeToExtension = MimeTypes.mimeTypeToExtensions
        private val extensionToMimeType = MimeTypes.extensionToMimeType
    }

    sealed class DownloadCheckResult : LogDumpable {
        class Downloadable(private val fileName: String, private val extension: String?) : DownloadCheckResult() {
            fun toFileName() = "$fileName.$extension"

            override fun dump(stringBuilder: StringBuilder, hasher: LogHasher) = stringBuilder.curlyWrapped {
                commaSeparated {
                    item { append("type=downloadable") }
                    item { hasher.hash(stringBuilder, "name=", fileName, FileNameProcessor) }
                    item { hasher.hash(stringBuilder, "ext=", fileName, FileExtensionProcessor) }
                }
            }
        }

        data object NonDownloadable : DownloadCheckResult() {
            override fun dump(stringBuilder: StringBuilder, hasher: LogHasher) = stringBuilder.curlyWrapped {
                append("type=non_downloadable")
            }
        }

        data object MimeTypeDetectionFailed : DownloadCheckResult() {
            override fun dump(stringBuilder: StringBuilder, hasher: LogHasher) = stringBuilder.curlyWrapped {
                append("type=failed")
            }
        }

        fun isDownloadable() = this is Downloadable
    }

    fun checkIsNonHtmlFileEnding(url: String): DownloadCheckResult {
        val (fileName, ext) = Extension.getFileNameFromUrl(URL(url))

        if (fileName == null || ext == null) {
            return DownloadCheckResult.MimeTypeDetectionFailed
        }

        val mimeType = extensionToMimeType[ext]
            ?: return DownloadCheckResult.MimeTypeDetectionFailed

        return checkMimeType(mimeType, fileName, ext)
    }

    fun isNonHtmlContentUri(url: String, timeout: Int): DownloadCheckResult {
        val contentType = try {
            cachedRequest.head(url, timeout, false).findHeader(CONTENT_TYPE_HEADER)
        } catch (e: IOException) {
            logger.error(e)
            return DownloadCheckResult.NonDownloadable
        }

        val firstContentType = contentType?.values?.firstOrNull()
            ?: return DownloadCheckResult.NonDownloadable

        val (fileName, _) = Extension.getFileNameFromUrl(URL(url))

        val mimeType = firstContentType.split(";")[0]
        return checkMimeType(
            mimeType,
            fileName ?: url,
            mimeTypeToExtension[mimeType]?.firstOrNull()
        )
    }

    private fun checkMimeType(mimeType: String, fileName: String, extension: String?): DownloadCheckResult {
        return if (mimeType != HTML_MIME_TYPE) DownloadCheckResult.Downloadable(fileName, extension)
        else DownloadCheckResult.NonDownloadable
    }
}
