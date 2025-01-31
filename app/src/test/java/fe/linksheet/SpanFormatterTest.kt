package fe.linksheet

import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.android.span.helper.formatter.SpanFormatter
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class SpanFormatterTest : LinkSheetTest {

    private fun compareHtml(
        input: SpannableStringBuilder.() -> Unit,
        vararg args: Any?,
        expected: SpannableStringBuilder.() -> Unit,
    ) {
        val result = Html.toHtml(SpanFormatter.format(buildSpannedString(input), *args), Html.FROM_HTML_MODE_LEGACY)
        val ex = Html.toHtml(buildSpannedString(expected), Html.FROM_HTML_MODE_LEGACY)

        assertEquals(ex, result)
    }

    @Test
    fun test() {
        compareHtml(
            input = { append("%s") },
            "henlo",
            expected = { append("henlo") }
        )

        compareHtml(
            input = { bold { append("hello world") } },
            expected = { bold { append("hello world") } }
        )

        compareHtml(
            input = { bold { append("%s") } },
            "world",
            expected = { bold { append("world") } }
        )

        compareHtml(
            input = {
                bold { append("%s") }.append("bar")
            },
            "foo",
            expected = {
                bold { append("foo") }.append("bar")
            }
        )

        compareHtml(
            input = {
                bold { append("foo") }.append("%s")
            },
            "bar",
            expected = {
                bold { append("foo") }.append("bar")
            }
        )
    }
}
