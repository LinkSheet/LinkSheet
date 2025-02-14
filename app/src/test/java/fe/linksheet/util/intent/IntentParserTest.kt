package fe.linksheet.util.intent

import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.tableOf
import fe.linksheet.LinkSheetTest
import mozilla.components.support.utils.toSafeIntent
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class IntentParserTest : LinkSheetTest {
    @Test
    fun `test view intent parsing`() {
        tableOf("intent", "expected")
            .row(
                buildIntent(Intent.ACTION_VIEW, Uri.parse("http://data.com")),
                Uri.parse("http://data.com")
            )
            .row(
                buildIntent(Intent.ACTION_VIEW) {
                    putExtra(Intent.EXTRA_TEXT, "http://extra-text.com")
                },
                Uri.parse("http://extra-text.com")
            )
            .row(
                buildIntent(Intent.ACTION_VIEW) {
                    putExtra(Intent.EXTRA_PROCESS_TEXT, "http://extra-process-text.com")
                },
                Uri.parse("http://extra-process-text.com")
            )
            .forAll { intent, expected ->
                assertThat(IntentParser.parseViewAction(intent.toSafeIntent())).isEqualTo(expected)
            }
    }

    @Test
    fun `test text parsing`() {
        val result = IntentParser.parseText("foo bar google.com hello world")
        assertThat(result).isEqualTo(Uri.parse("http://google.com"))
    }
}
