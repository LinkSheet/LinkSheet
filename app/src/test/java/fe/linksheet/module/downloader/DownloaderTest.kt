package fe.linksheet.module.downloader

import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.httpkt.Request
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.file.DebugLogPersistService
import fe.linksheet.module.log.internal.DefaultLoggerDelegate
import fe.linksheet.module.redactor.LogHasher
import fe.linksheet.module.redactor.Redactor
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.testlib.core.RobolectricTest
import fe.std.uri.toStdUrlOrThrow
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunWith(AndroidJUnit4::class)
internal class DownloaderTest : RobolectricTest {
    companion object {
        private val loggerDelegate = DefaultLoggerDelegate(
            true,
            "test",
            Redactor(LogHasher.NoOpHasher),
            DebugLogPersistService()
        )
        private val logger = Logger(loggerDelegate)
        private val downloader = Downloader(
            CachedRequest(Request(), logger),
            logger
        )
    }

    @JunitTest
    fun testCheckIsNonHtmlFileEnding() {
        mapOf(
            DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com",
            DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com/",
            DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com/yeet",
            DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com/yeet.",
            DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com/.yeet",
        ).forEach { (expected, inputUrl) ->
            assertEquals(expected, downloader.checkIsNonHtmlFileEnding(inputUrl.toStdUrlOrThrow()))
        }

        mapOf(
            "test.jpg" to "https://test.com/test.jpg",
            "test.yeet.jpg" to "https://test.com/test.yeet.jpg",
        ).forEach { (expectedFile, inputUrl) ->
            val downloadable = downloader.checkIsNonHtmlFileEnding(inputUrl.toStdUrlOrThrow())
            assertIs<DownloadCheckResult.Downloadable>(downloadable)
            assertEquals(expectedFile, downloadable.toFileName())
        }
    }

    @JunitTest
    fun testIsNonHtmlContentUri() {
        val downloadable = downloader.isNonHtmlContentUri(
            "https://pbs.twimg.com/media/FyWt0wvWAAAxgYk?format=jpg&name=medium".toStdUrlOrThrow(),
            15
        )

        assertIs<DownloadCheckResult.Downloadable>(downloadable)
        assertEquals("FyWt0wvWAAAxgYk.jpg", downloadable.toFileName())

        val nonDownloadable = downloader.isNonHtmlContentUri("https://github.com".toStdUrlOrThrow(), 15)
        assertIs<DownloadCheckResult.NonDownloadable>(nonDownloadable)
    }
}
