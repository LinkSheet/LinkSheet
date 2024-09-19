package fe.linksheet

import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.downloader.downloaderModule
import fe.linksheet.module.log.internal.DebugLoggerDelegate
import fe.linksheet.module.request.requestModule
import fe.linksheet.module.resolver.urlresolver.cachedRequestModule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunWith(AndroidJUnit4::class)
class DownloaderTest : AutoCloseKoinTest() {
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            DebugLoggerDelegate.Factory,
            requestModule,
            cachedRequestModule,
            downloaderModule
        )
    }

    private val downloader by inject<Downloader>()

    @Test
    fun testCheckIsNonHtmlFileEnding() {
        mapOf(
            DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com",
            DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com/",
            DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com/yeet",
            DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com/yeet.",
            DownloadCheckResult.MimeTypeDetectionFailed to "https://test.com/.yeet",
        ).forEach { (expected, inputUrl) ->
            assertEquals(expected, downloader.checkIsNonHtmlFileEnding(inputUrl))
        }

        mapOf(
            "test.jpg" to "https://test.com/test.jpg",
            "test.yeet.jpg" to "https://test.com/test.yeet.jpg",
        ).forEach { (expectedFile, inputUrl) ->
            val downloadable = downloader.checkIsNonHtmlFileEnding(inputUrl)
            assertIs<DownloadCheckResult.Downloadable>(downloadable)
            assertEquals(expectedFile, downloadable.toFileName())
        }
    }

    @Test
    fun testIsNonHtmlContentUri() {
        val downloadable = downloader.isNonHtmlContentUri(
            "https://pbs.twimg.com/media/FyWt0wvWAAAxgYk?format=jpg&name=medium",
            15
        )

        assertIs<DownloadCheckResult.Downloadable>(downloadable)
        assertEquals("FyWt0wvWAAAxgYk.jpg", downloadable.toFileName())

        val nonDownloadable = downloader.isNonHtmlContentUri("https://github.com", 15)
        assertIs<DownloadCheckResult.NonDownloadable>(nonDownloadable)
    }
}
