package app.linksheet.feature.app.linksheet.feature.downloader

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.api.CachedRequest
import app.linksheet.feature.downloader.DownloadCheckResult
import app.linksheet.feature.downloader.Downloader
import fe.httpkt.Request
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.uri.toStdUrlOrThrow
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.net.HttpURLConnection
import kotlin.test.assertEquals
import kotlin.test.assertIs



@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class DownloaderTest : BaseUnitTest {
    companion object {
        private val request = Request()
        private val downloader = Downloader(
            object : CachedRequest {
                override fun head(
                    url: String,
                    timeout: Int,
                    followRedirects: Boolean
                ): HttpURLConnection {
                    return request.head(url)
                }

                override fun get(
                    url: String,
                    timeout: Int,
                    followRedirects: Boolean
                ): HttpURLConnection {
                    return request.get(url)
                }
            }
        )
    }

    @Test
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

    @Test
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
