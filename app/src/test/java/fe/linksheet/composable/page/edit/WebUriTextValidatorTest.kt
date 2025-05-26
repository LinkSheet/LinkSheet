package fe.linksheet.composable.page.edit

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.tableOf
import fe.linksheet.testlib.core.RobolectricTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class WebUriTextValidatorTest : RobolectricTest {

    @Test
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
