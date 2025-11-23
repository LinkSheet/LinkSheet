package app.linksheet.feature.app.linksheet.feature.downloader

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.api.CachedRequest
import app.linksheet.feature.downloader.DownloadCheckResult
import app.linksheet.feature.downloader.Downloader
import fe.httpkt.HttpData
import fe.httpkt.Request
import fe.httpkt.data.header.HttpContentType
import fe.httpkt.data.header.HttpHeader
import fe.httpkt2.Url
import fe.httpkt2.test.MockHttpConnection
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.uri.toStdUrlOrThrow
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import kotlin.test.assertEquals
import kotlin.test.assertIs


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class DownloaderTest : BaseUnitTest {
    companion object {
        private val request = Request()
        private val FyWt0wvWAAAxgYk = byteArrayOf(
            -1, -40, -1, -32, 0, 16, 74, 70, 73, 70, 0, 1, 1, 1, 1, 44, 1, 44, 0, 0, -1, -37, 0, 67, 0, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1, -37, 0, 67, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1, -62, 0, 17, 8, 0, 10, 0, 10, 3, 1, 17, 0, 2, 17, 1, 3, 17,
            1, -1, -60, 0, 21, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, -1, -60, 0, 22, 1, 1, 1, 1, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 9, -1, -38, 0, 12, 3, 1, 0, 2, 16, 3, 16, 0, 0, 1, -117, -27, 61, -4, 0,
            127, -1, -60, 0, 20, 16, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, -1, -38, 0, 8, 1, 1, 0, 1, 5, 2,
            31, -1, -60, 0, 20, 17, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, -1, -38, 0, 8, 1, 3, 1, 1, 63, 1, 31,
            -1, -60, 0, 20, 17, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, -1, -38, 0, 8, 1, 2, 1, 1, 63, 1, 31, -1,
            -60, 0, 20, 16, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, -1, -38, 0, 8, 1, 1, 0, 6, 63, 2, 31, -1, -60,
            0, 20, 16, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, -1, -38, 0, 8, 1, 1, 0, 1, 63, 33, 31, -1, -38,
            0, 12, 3, 1, 0, 2, 0, 3, 0, 0, 0, 16, 0, 15, -1, -60, 0, 20, 17, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 32, -1, -38, 0, 8, 1, 3, 1, 1, 63, 16, 31, -1, -60, 0, 20, 17, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 32, -1, -38, 0, 8, 1, 2, 1, 1, 63, 16, 31, -1, -60, 0, 20, 16, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 32, -1, -38, 0, 8, 1, 1, 0, 1, 63, 16, 31, -1, -39
        )

        private val downloader = Downloader(
            object : CachedRequest {
                override fun head(
                    url: String,
                    timeout: Int,
                    followRedirects: Boolean
                ): HttpURLConnection {
                    if (url.contains("FyWt0wvWAAAxgYk")) {
                        return MockHttpConnection(
                            url = Url(url),
                            responseCode = 200,
                            inputStream = ByteArrayInputStream(FyWt0wvWAAAxgYk),
                            httpData = HttpData.of {
                                addHttpHeadersOf(HttpHeader.ContentType to "image/jpeg")
                            }
                        )
                    }
                    if (url == "https://github.com") {
                        return MockHttpConnection(
                            url = Url(url),
                            responseCode = 200,
                            httpData = HttpData.of {
                                addHttpHeadersOf(HttpHeader.ContentType to HttpContentType.TextHtml.value)
                            }
                        )
                    }

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
           0
        )

        assertIs<DownloadCheckResult.Downloadable>(downloadable)
        assertEquals("FyWt0wvWAAAxgYk.jpg", downloadable.toFileName())

        val nonDownloadable = downloader.isNonHtmlContentUri("https://github.com".toStdUrlOrThrow(), 0)
        assertIs<DownloadCheckResult.NonDownloadable>(nonDownloadable)
    }
}
