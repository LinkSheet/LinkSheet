package fe.linksheet.module.downloader

import fe.httpkt.ext.findHeader
import fe.httpkt.util.Extension
import fe.linksheet.module.log.hasher.FileExtensionProcessor
import fe.linksheet.module.log.hasher.FileNameProcessor
import fe.linksheet.module.log.hasher.LogDumpable
import fe.linksheet.module.log.hasher.LogHasher
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.mimetypekt.MimeTypes
import fe.stringbuilder.util.commaSeparated
import fe.stringbuilder.util.curlyWrapped
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.io.IOException
import java.net.URL


val downloaderModule = module {
    singleOf(::Downloader)
}

class Downloader(
    private val cachedRequest: CachedRequest,
    loggerFactory: LoggerFactory,
) {
    private val logger = loggerFactory.createLogger(Downloader::class)

    companion object {
        private const val contentTypeHeader = "Content-Type"
        private const val textHtml = "text/html"

        private val mimeTypeToExtension = MimeTypes.mimeTypeToExtensions
        private val extensionToMimeType = MimeTypes.extensionToMimeType
    }

    sealed class DownloadCheckResult : LogDumpable {
        class Downloadable(
            private val fileName: String,
            private val extension: String?
        ) : DownloadCheckResult() {
            fun toFileName() = "$fileName.$extension"
            override fun dump(
                stringBuilder: StringBuilder,
                hasher: LogHasher
            ) = stringBuilder.curlyWrapped {
                commaSeparated {
                    item { append("type=downloadable") }
                    item { hasher.hash(stringBuilder, "name=", fileName, FileNameProcessor) }
                    item { hasher.hash(stringBuilder, "ext=", fileName, FileExtensionProcessor) }
                }
            }
        }

        data object NonDownloadable : DownloadCheckResult() {
            override fun dump(
                stringBuilder: StringBuilder,
                hasher: LogHasher
            ) = stringBuilder.curlyWrapped {
                append("type=non_downloadable")
            }
        }

        data object MimeTypeDetectionFailed : DownloadCheckResult() {
            override fun dump(
                stringBuilder: StringBuilder,
                hasher: LogHasher
            ) = stringBuilder.curlyWrapped {
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
            cachedRequest.head(url, timeout, false).findHeader(contentTypeHeader)
        } catch (e: IOException) {
            logger.debug(e)
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

    private fun checkMimeType(
        mimeType: String,
        fileName: String,
        extension: String?
    ) = if (mimeType != textHtml) {
        DownloadCheckResult.Downloadable(fileName, extension)
    } else DownloadCheckResult.NonDownloadable
}
