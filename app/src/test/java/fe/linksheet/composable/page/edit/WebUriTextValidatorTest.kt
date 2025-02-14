package fe.linksheet.composable.page.edit

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.tableOf
import fe.linksheet.UnitTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class WebUriTextValidatorTest : UnitTest {

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
