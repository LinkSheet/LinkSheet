package fe.linksheet.module.downloader

import fe.httpkt.util.findHeader
import fe.linksheet.extension.substringNullable
import fe.linksheet.module.log.FileExtensionProcessor
import fe.linksheet.module.log.FileNameProcessor
import fe.linksheet.module.log.LogDumpable
import fe.linksheet.module.log.LogHasher
import fe.linksheet.module.request.requestModule
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.mimetypekt.MimeTypeLoader
import fe.stringbuilder.util.commaSeparated
import fe.stringbuilder.util.curlyWrapped
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.io.IOException
import kotlin.math.min


val downloaderModule = module {
    singleOf(::Downloader)
}

class Downloader(private val cachedRequest: CachedRequest) {
    companion object {
        private const val contentTypeHeader = "Content-Type"
        private const val textHtml = "text/html"
        private const val schemeSeparator = "://"

        private val mappings = MimeTypeLoader.loadBuiltInMimeTypes()
    }

    sealed class DownloadCheckResult : LogDumpable {
        class Downloadable(private val fileName: String, private val extension: String?) :
            DownloadCheckResult() {
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

        object NonDownloadable : DownloadCheckResult() {
            override fun dump(
                stringBuilder: StringBuilder,
                hasher: LogHasher
            ) = stringBuilder.curlyWrapped {
                append("type=non_downloadable")
            }
        }

        object MimeTypeDetectionFailed : DownloadCheckResult() {
            override fun dump(
                stringBuilder: StringBuilder,
                hasher: LogHasher
            ) = stringBuilder.curlyWrapped {
                append("type=failed")
            }
        }

        fun isDownloadable() = this is Downloadable
    }

    private fun urlToFilename(url: String): String {
        val query = url.indexOf("?").takeIf { it > -1 } ?: url.length
        val dot = url.lastIndexOf(".").takeIf { it > -1 } ?: url.length

        return url.substringNullable(url.lastIndexOf("/") + 1, min(query, dot)) ?: url
    }

    fun checkIsNonHtmlFileEnding(url: String): DownloadCheckResult {
        val scheme = url.indexOf(schemeSeparator).takeIf { it > -1 }
            ?: return DownloadCheckResult.MimeTypeDetectionFailed

        if (url.indexOf("/", scheme + schemeSeparator.length) == -1) {
            // .com is a file extension for the mime type x-msdos-program
            return DownloadCheckResult.MimeTypeDetectionFailed
        }

        val extension = url.substringNullable(url.lastIndexOf(".") + 1)
            ?: return DownloadCheckResult.MimeTypeDetectionFailed

        val mimeType = mappings.second.map[extension]
            ?: return DownloadCheckResult.MimeTypeDetectionFailed

        return checkMimeType(mimeType, url, extension)
    }

    fun isNonHtmlContentUri(url: String, timeout: Int): DownloadCheckResult {
        val contentType = try {
            cachedRequest.head(url, timeout, false).findHeader(contentTypeHeader)
        } catch (e: IOException) {
            return DownloadCheckResult.NonDownloadable
        }

        val firstContentType = contentType?.values?.firstOrNull()
            ?: return DownloadCheckResult.NonDownloadable

        val mimeType = firstContentType.split(";")[0]
        return checkMimeType(
            mimeType,
            url,
            mappings.first.map[mimeType]?.firstOrNull()
        )
    }

    private fun checkMimeType(
        mimeType: String,
        url: String,
        extension: String?
    ): DownloadCheckResult {
        return if (mimeType != textHtml) {
            DownloadCheckResult.Downloadable(urlToFilename(url), extension)
        } else DownloadCheckResult.NonDownloadable
    }
}

fun main() {
    startKoin {
        modules(requestModule, downloaderModule)
    }

    object : KoinComponent {
        private val downloader by inject<Downloader>()

        init {
            println(downloader.checkIsNonHtmlFileEnding("https://test.com"))
            println(downloader.checkIsNonHtmlFileEnding("https://test.com/"))
            println(downloader.checkIsNonHtmlFileEnding("https://test.com/test.com"))
        }
    }
}