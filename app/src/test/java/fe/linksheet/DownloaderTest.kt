package fe.linksheet

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.downloader.downloaderModule
import fe.linksheet.module.log.internal.DebugLoggerDelegate
import fe.linksheet.module.request.requestModule
import fe.linksheet.module.resolver.urlresolver.cachedRequestModule
import fe.linksheet.util.KoinTestRuleFix
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class DownloaderTest : KoinTest {
    @get:Rule
    val koinTestRule = KoinTestRuleFix.create {
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

    @After
    fun teardown() = stopKoin()
}
