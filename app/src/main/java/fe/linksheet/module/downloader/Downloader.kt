package fe.linksheet.module.downloader

import fe.httpkt.Request
import fe.httpkt.util.findHeader
import fe.linksheet.extension.substringNullable
import fe.linksheet.module.request.requestModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.net.URLConnection
import fe.mimetypekt.MimeTypeLoader
import kotlin.math.min


val downloaderModule = module {
    single {
        Downloader(get())
    }
}

class Downloader(private val request: Request) {
    companion object {
        private const val contentTypeHeader = "Content-Type"
        private const val textHtml = "text/html"
        private const val schemeSeparator = "://"

        private val mappings = MimeTypeLoader.loadBuiltInMimeTypes()
    }

    sealed class DownloadCheckResult {
        class Downloadable(private val fileName: String, private val extension: String?) :
            DownloadCheckResult() {
            fun toFileName() = "$fileName.$extension"
        }

        object NonDownloadable : DownloadCheckResult()
        object MimeTypeDetectionFailed : DownloadCheckResult()

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

    fun isNonHtmlContentUri(url: String): DownloadCheckResult {
        val contentType = request.head(url).findHeader(contentTypeHeader)
        val firstContentType =
            contentType?.values?.firstOrNull() ?: return DownloadCheckResult.NonDownloadable
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