package fe.linksheet

import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.downloader.downloaderModule
import fe.linksheet.module.log.factory.DebugLoggerFactory
import fe.linksheet.module.request.requestModule
import fe.linksheet.module.resolver.urlresolver.cachedRequestModule
import org.junit.Test
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DownloaderTest : KoinTest {
    private val downloader: Downloader

    init {
        startKoin {
            modules(DebugLoggerFactory.module, requestModule, cachedRequestModule, downloaderModule)
        }

        downloader = get()
    }

    @Test
    fun testCheckIsNonHtmlFileEnding() {
        mapOf(
            Downloader.DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com",
            Downloader.DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com/",
            Downloader.DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com/yeet",
            Downloader.DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com/yeet.",
            Downloader.DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com/.yeet",
        ).forEach { (expected, inputUrl) ->
            assertEquals(expected, downloader.checkIsNonHtmlFileEnding(inputUrl))
        }

        mapOf(
            "test.jpg" to "https://test.com/test.jpg",
            "test.yeet.jpg" to "https://test.com/test.yeet.jpg",
        ).forEach { (expectedFile, inputUrl) ->
            val downloadable = downloader.checkIsNonHtmlFileEnding(inputUrl)
            assertIs<Downloader.DownloadCheckResult.Downloadable>(downloadable)
            assertEquals(expectedFile, downloadable.toFileName())
        }
    }

    @Test
    fun testIsNonHtmlContentUri() {
        val downloadable = downloader.isNonHtmlContentUri(
            "https://pbs.twimg.com/media/FyWt0wvWAAAxgYk?format=jpg&name=medium",
            15
        )

        assertIs<Downloader.DownloadCheckResult.Downloadable>(downloadable)
        assertEquals("FyWt0wvWAAAxgYk.jpg", downloadable.toFileName())

        val nonDownloadable = downloader.isNonHtmlContentUri("https://github.com", 15)
        assertIs<Downloader.DownloadCheckResult.NonDownloadable>(nonDownloadable)
    }
}
