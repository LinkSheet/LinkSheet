package fe.linksheet.util.intent.parser

import android.content.Intent
import android.net.Uri
import android.nfc.NfcAdapter
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.composekit.intent.buildIntent
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.result.assert.assertSuccess
import fe.std.result.getOrNull
import fe.std.test.TestFunction
import fe.std.test.tableTest
import mozilla.components.support.utils.toSafeIntent
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class IntentParserTest : BaseUnitTest {
    @org.junit.Test
    fun `test nfc intent correctly handled`() {
        val uri = Uri.parse("https://linksheet.app")
        val intent = Intent(NfcAdapter.ACTION_NDEF_DISCOVERED, uri).toSafeIntent()

        assertSuccess(IntentParser.getUriFromIntent(intent)).isEqualTo(uri)
    }

    @org.junit.Test
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

    @org.junit.Test
    fun `test text parsing`() {
        val result = IntentParser.parseText("foo bar google.com hello world").getOrNull()?.toString()
        assertThat(result).isEqualTo("http://google.com")
    }

//    @org.junit.Test
//    fun `test send intent parsing`() {
//        // TODO
//    }
}



