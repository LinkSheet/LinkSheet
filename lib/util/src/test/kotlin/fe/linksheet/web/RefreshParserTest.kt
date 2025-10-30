package fe.linksheet.web

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.tableOf
import fe.linksheet.testlib.core.BaseUnitTest
import org.jsoup.Jsoup
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class RefreshParserTest : BaseUnitTest {
    companion object {
    }

    private val validRefreshData = tableOf("header", "expected")
        .row("0,https://linkin.bio/google", 0 to "https://linkin.bio/google")
        .row("0;https://linkin.bio/google", 0 to "https://linkin.bio/google")
        .row("0;URL=https://linkin.bio/google", 0 to "https://linkin.bio/google")
        .row("1;URL=https://linkin.bio/google", 1 to "https://linkin.bio/google")
        .row("1.1;URL=https://linkin.bio/google", 1 to "https://linkin.bio/google")
        .row("0;url=https://linkin.bio/google", 0 to "https://linkin.bio/google")
        .row("0;url=\"https://linkin.bio/google\"", 0 to "https://linkin.bio/google")
        .row("0;url='https://linkin.bio/google'", 0 to "https://linkin.bio/google")
        .row("0; URL='https://cnn.com/'", 0 to "https://cnn.com/")
        // Parser doesn't do validation beyond basic length checks, so this is valid
        .row("0;url=ht", 0 to "ht")

    private val invalidRefreshData =
        listOf("1.1", "1.1;", "1", "1;", ";", ";;;", ";URL=", ";url=", "; url=", "; url =", ";;url=")

    @org.junit.Test
    fun `test parse valid refresh header`() {
        validRefreshData.forAll { header, expected ->
            assertThat(RefreshParser.matchRefreshContent(header)).isEqualTo(expected)
        }
    }

    @org.junit.Test
    fun `test parse invalid refresh header`() {
        invalidRefreshData.forEach {
            assertThat(RefreshParser.matchRefreshContent(it)).isNull()
        }
    }

    @org.junit.Test
    fun test() {
        val html = Jsoup.parse(
            """<html>
            <head>
                <meta http-equiv="refresh" content="0; URL='https://cnn.com/'" />
                <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate, max-age=0" />
                <meta http-equiv="Pragma" content="no-cache" />
                <meta http-equiv="Expires" content="0" />
                <style>:root { color-scheme: light dark; }</style>
            </head>
        </html>"""
        )

        assertThat(RefreshParser.parseHtml(html)).isEqualTo(0 to "https://cnn.com/")
    }
}
