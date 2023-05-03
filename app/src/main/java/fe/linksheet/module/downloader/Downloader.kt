package fe.linksheet.module.downloader

import fe.httpkt.Request
import fe.httpkt.util.findHeader
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

        private val fileNameMap = URLConnection.getFileNameMap()
        private val mimeTypes = MimeTypeLoader.loadBuiltInMimeTypes(
            MimeTypeLoader.Mapping.MimeTypeToExtensions
        ) as MimeTypeLoader.MimeTypeHolder.MimeTypeToExtensions
    }

    sealed class DownloadCheckResult {
        class Downloadable(private val fileName: String, private val fileType: String?) :
            DownloadCheckResult() {
            fun toFileName() = "$fileName$fileType"
        }

        object NonDownloadable : DownloadCheckResult()
        object MimeTypeDetectionFailed : DownloadCheckResult()

        fun isDownloadable() = this is Downloadable
    }

    private fun urlToFilename(url: String): String {
        val query = url.indexOf("?").takeIf { it > -1 } ?: url.length
        val dot = url.lastIndexOf(".").takeIf { it > -1 } ?: url.length

        return url.substring(url.lastIndexOf("/") + 1, min(query, dot))
    }

    fun checkIsNonHtmlFileEnding(url: String): DownloadCheckResult {
        val extension = url.substring(url.lastIndexOf("."))
        val mimeType = fileNameMap.getContentTypeFor(
            "lol${extension}"
        ) ?: return DownloadCheckResult.MimeTypeDetectionFailed

        return checkMimeType(mimeType, url, extension)
    }

    fun isNonHtmlContentUri(url: String): DownloadCheckResult {
        val contentType = request.head(url).findHeader(contentTypeHeader)
        val firstContentType =
            contentType?.values?.firstOrNull() ?: return DownloadCheckResult.NonDownloadable
        val mimeType = firstContentType.split(";")[0]

        return checkMimeType(mimeType, url, mimeTypes.map[mimeType]?.firstOrNull()?.let { ".$it" })
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
            println(downloader.isNonHtmlContentUri("https://pbs.twimg.com/media/FvIdhC2WcAg3yHx?format=jpg&name=small"))
            println(downloader.checkIsNonHtmlFileEnding("https://nitter.net/pic/orig/media%2FFvIdhC2WcAg3yHx.jpg"))
        }
    }
}