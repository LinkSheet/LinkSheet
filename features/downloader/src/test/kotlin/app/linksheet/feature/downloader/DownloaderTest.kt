package app.linksheet.feature.app.linksheet.feature.downloader

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.downloader.DownloadCheckResult
import app.linksheet.feature.downloader.Downloader
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.uri.toStdUrlOrThrow
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertIs


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class DownloaderTest : BaseUnitTest {
    companion object {
        private val FyWt0wvWAAAxgYk = byteArrayOf(
            -1, -40, -1, -32, 0, 16, 74, 70, 73, 70, 0, 1, 1, 1, 1, 44, 1, 44, 0, 0, -1, -37, 0, 67, 0, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1, -37, 0, 67, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        )
        private const val CONTENT_DISPOSITION_TEST_TOKEN = "CONTENT_DISPOSITION_TEST"
        private val downloader = Downloader(
            HttpClient(MockEngine {
                val urlStr = it.url.toString()
                when {
                    urlStr.contains("FyWt0wvWAAAxgYk") -> {
                        return@MockEngine respond(content = FyWt0wvWAAAxgYk, headers = headersOf(HttpHeaders.ContentType, "image/jpeg"))
                    }
                    urlStr.contains(CONTENT_DISPOSITION_TEST_TOKEN) -> {
                        return@MockEngine respond(
                            content = FyWt0wvWAAAxgYk,
                            headers = headersOf(
                                HttpHeaders.ContentType to listOf("image/jpeg"),
                                HttpHeaders.ContentDisposition to listOf("Content-Disposition: attachment; filename=\"$CONTENT_DISPOSITION_TEST_TOKEN.jpeg\"")
                            )
                        )
                    }
                    urlStr == "https://github.com" -> {
                        return@MockEngine respond(content = "<html></html>")
                    }

                    else -> {
                        respondOk()
                    }
                }
            })
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

    }

    @Test
    fun `test checkIsNonHtmlFileEnding downloadable`() {
        mapOf(
            "test.jpg" to "https://test.com/test.jpg",
            "test.yeet.jpg" to "https://test.com/test.yeet.jpg",
        ).forEach { (expectedFile, inputUrl) ->
            val downloadable = downloader.checkIsNonHtmlFileEnding(inputUrl.toStdUrlOrThrow())
            assertIs<DownloadCheckResult.Downloadable>(downloadable)
            assertEquals(expectedFile, downloadable.fileName)
        }
    }

    @Test
    fun testIsNonHtmlContentUri() = runTest {
        val downloadable = downloader.isNonHtmlContentUri(
            "https://pbs.twimg.com/media/FyWt0wvWAAAxgYk?format=jpg&name=medium".toStdUrlOrThrow(),
        )

        assertIs<DownloadCheckResult.Downloadable>(downloadable)
        assertEquals("FyWt0wvWAAAxgYk.jpg", downloadable.fileName)

        val nonDownloadable = downloader.isNonHtmlContentUri("https://github.com".toStdUrlOrThrow())
        assertIs<DownloadCheckResult.NonDownloadable>(nonDownloadable)
    }

    @Test
    fun `test content disposition header handling`() = runTest {
        val downloadable = downloader.isNonHtmlContentUri(
            "https://pbs.twimg.com/media/${CONTENT_DISPOSITION_TEST_TOKEN}?format=jpg&name=medium".toStdUrlOrThrow(),
        )

        assertIs<DownloadCheckResult.Downloadable>(downloadable)
        assertEquals("${CONTENT_DISPOSITION_TEST_TOKEN}.jpeg", downloadable.fileName)
    }

    @Test
    fun `test findExtension`()  {

    }
}
