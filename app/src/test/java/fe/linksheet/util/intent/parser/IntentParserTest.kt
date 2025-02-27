package fe.linksheet.util.intent.parser

import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.linksheet.UnitTest
import fe.linksheet.util.intent.buildIntent
import fe.std.result.getOrNull
import fe.std.test.TestFunction
import fe.std.test.tableTest
import mozilla.components.support.utils.toSafeIntent
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class IntentParserTest : UnitTest {

    @Test
    fun `test view intent parsing`() {
        fun buildTestIntent(url: String, extra: String?): Intent {
            val uri = if (extra == null) Uri.parse(url) else null

            return buildIntent(Intent.ACTION_VIEW, uri) {
                extra?.let { putExtra(it, url) }
            }
        }

        tableTest<String, String?>("url", "extra")
            .row("http://data.com", null)
            .row("http://extra-text.com", Intent.EXTRA_TEXT)
            .row("http://extra-process-text.com", Intent.EXTRA_PROCESS_TEXT)
            .prepare(column3 = "intent") { url, extra ->
                buildTestIntent(url, extra)
            }
            .test(TestFunction<Intent, String?> { intent ->
                IntentParser.parseViewAction(intent.toSafeIntent()).getOrNull()?.toString()
            })
            .forAll { test, url, extra, intent ->
                assertThat(test.run(intent)).isEqualTo(url)
            }
    }

    @Test
    fun `test text parsing`() {
        val result = IntentParser.parseText("foo bar google.com hello world").getOrNull()?.toString()
        assertThat(result).isEqualTo("http://google.com")
    }

    @Test
    fun `test send intent parsing`() {
        // TODO
    }
}



