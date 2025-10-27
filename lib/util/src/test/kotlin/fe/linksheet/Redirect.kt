package fe.linksheet

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.tableOf
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class RedirectResolveRequestTest : BaseUnitTest {
    companion object {
    }

    @org.junit.Test
    fun `test parse valid refresh header`() {
        tableOf("header", "expected")
            .row("0,https://linkin.bio/google", 0 to "https://linkin.bio/google")
            .row("0;https://linkin.bio/google", 0 to "https://linkin.bio/google")
            .row("0;URL=https://linkin.bio/google", 0 to "https://linkin.bio/google")
            .row("1;URL=https://linkin.bio/google", 1 to "https://linkin.bio/google")
            .row("1.1;URL=https://linkin.bio/google", 1 to "https://linkin.bio/google")
            .row("0;url=https://linkin.bio/google", 0 to "https://linkin.bio/google")
            .row("0;url=\"https://linkin.bio/google\"", 0 to "https://linkin.bio/google")
            .row("0;url='https://linkin.bio/google'", 0 to "https://linkin.bio/google")
            // Parser doesn't do validation beyond basic length checks, so this is valid
            .row("0;url=ht", 0 to "ht")
            .forAll { header, expected ->
                assertThat(RedirectResolver.parseRefreshHeader(header)).isEqualTo(expected)
            }
    }

    @org.junit.Test
    fun `test parse invalid refresh header`() {
        tableOf("header", "expected")
            .row<String, Pair<Int?, String?>?>("1.1", null)
            .row("1.1;", null)
            .row("1", null)
            .row("1;", null)
            .row(";", null)
            .row(";;;", null)
            .row(";URL=", null)
            .row(";url=", null)
            .row("; url=", null)
            .row("; url =", null)
            .row(";;url=", null)
            .forAll { header, expected ->
                assertThat(RedirectResolver.parseRefreshHeader(header)).isEqualTo(expected)
            }
    }
}
