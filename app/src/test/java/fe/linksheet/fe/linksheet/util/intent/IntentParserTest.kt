package fe.linksheet.fe.linksheet.util.intent

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.linksheet.util.intent.IntentParser
import fe.linksheet.util.intent.buildIntent
import mozilla.components.support.utils.SafeIntent
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class IntentParserTest {
    @Test
    fun `test search intent parsing`() {

    }

    @Test
    fun `test text parsing`() {
        val result = IntentParser.parseText("foo bar google.com hello world")
        assertThat(result).isEqualTo(Uri.parse("http://google.com"))
    }
}
