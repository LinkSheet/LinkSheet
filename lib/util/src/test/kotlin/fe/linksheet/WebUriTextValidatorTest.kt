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
internal class WebUriTextValidatorTest : BaseUnitTest {

    @org.junit.Test
    fun `test web uri text validator`() {
        tableOf("text", "expected")
            .row("https://google.com", true)
            .row("http://google.com", true)
            .row("http://127.0.0.1", true)
            .row("google.com", false)
            .row("google", false)
            .forAll { text, expected ->
                assertThat(WebUriTextValidator.isValid(text)).isEqualTo(expected)
            }
    }
}
