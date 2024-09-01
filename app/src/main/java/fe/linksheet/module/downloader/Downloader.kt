package fe.linksheet.module.downloader

import fe.httpkt.ext.isHttpSuccess
import fe.httpkt.util.Extension
import fe.linksheet.extension.java.normalizedContentType
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.module.log.Logger
import fe.linksheet.module.redactor.Redactable
import fe.linksheet.module.redactor.Redactor
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.util.KnownMimeTypes
import fe.linksheet.util.MimeType
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

sealed class DownloadCheckResult : Redactable<DownloadCheckResult> {
    class Downloadable(private val fileName: String, private val extension: String?) : DownloadCheckResult() {
        fun toFileName() = "$fileName.$extension"

        override fun process(builder: StringBuilder, redactor: Redactor) = builder.curlyWrapped {
            commaSeparated {
                item { append("type=downloadable") }
//                        item { redactor.process(builder, fileName, FileNameProcessor, "name=") }
//                        item { redactor.process(builder, extension, FileExtensionProcessor, "ext=") }
            }
        }
    }

    data object NonDownloadable : DownloadCheckResult() {
        override fun process(builder: StringBuilder, redactor: Redactor) = builder.curlyWrapped {
            append("type=non_downloadable")
        }
    }

    data object MimeTypeDetectionFailed : DownloadCheckResult() {
        override fun process(builder: StringBuilder, redactor: Redactor) = builder.curlyWrapped {
            append("type=failed")
        }
    }

    fun isDownloadable() = this is Downloadable
}


class Downloader(private val cachedRequest: CachedRequest, private val logger: Logger) {

    companion object {
        private val mimeTypeToExtension = KnownMimeTypes.mimeTypeToExtensions
        private val extensionToMimeType = KnownMimeTypes.extensionToMimeType
    }

    fun checkIsNonHtmlFileEnding(url: String): DownloadCheckResult {
        val (fileName, ext) = Extension.getFileNameFromUrl(URL(url))
        if (fileName == null || ext == null) return DownloadCheckResult.MimeTypeDetectionFailed

        val mimeType = extensionToMimeType[ext] ?: return DownloadCheckResult.MimeTypeDetectionFailed
        return checkMimeType(mimeType, fileName, ext)
    }

    fun isNonHtmlContentUri(url: String, timeout: Int): DownloadCheckResult {
        val result = try {
            cachedRequest.head(url, timeout, false)
        } catch (e: IOException) {
            logger.error(e)
            return DownloadCheckResult.NonDownloadable
        }

        if (!result.isHttpSuccess()) return DownloadCheckResult.NonDownloadable

        val contentType = result.normalizedContentType() ?: return DownloadCheckResult.NonDownloadable
        val (fileName, _) = Extension.getFileNameFromUrl(URL(url))

        return checkMimeType(contentType, fileName ?: url, mimeTypeToExtension[contentType]?.firstOrNull())
    }

    private fun checkMimeType(mimeType: String, fileName: String, extension: String?): DownloadCheckResult {
        return if (mimeType != MimeType.TEXT_HTML) DownloadCheckResult.Downloadable(fileName, extension)
        else DownloadCheckResult.NonDownloadable
    }
}
