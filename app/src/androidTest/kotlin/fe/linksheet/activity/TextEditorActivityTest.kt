package fe.linksheet.activity

import android.app.Activity
import android.app.Instrumentation
import android.widget.EditText
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.launchActivityForResult
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import assertk.assertThat
import assertk.assertions.isEqualTo
import de.mannodermaus.junit5.compose.createAndroidComposeExtension
import fe.composekit.core.putEnumExtra
import fe.linksheet.composable.page.edit.EDITOR_APP_BAR_CANCEL_TEST_TAG
import fe.linksheet.composable.page.edit.EDITOR_APP_BAR_DONE_TEST_TAG
import fe.linksheet.testlib.core.ActivityInvoker
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalTestApi::class)
internal class TextEditorActivityTest : BaseUnitTest {
    @JvmField
    @RegisterExtension
    val extension = createAndroidComposeExtension {
        launchActivityForResult(ActivityInvoker.getIntentForActivity<TextEditorActivity> {
            putExtra(TextEditorActivity.Companion.EXTRA_TEXT, INPUT_TEXT)
            putEnumExtra(TextEditorActivity.Companion.EXTRA_SOURCE, TextEditorActivity.ExtraSource.ClipboardCard)
            putEnumExtra(
                TextEditorActivity.Companion.EXTRA_VALIDATOR,
                TextEditorActivity.ExtraValidator.WebUriTextValidator
            )
        })
    }

    private val INPUT_TEXT = "Hello World"
    private val INPUT_URL = "https://linksheet.app"

    private fun Instrumentation.ActivityResult.assertValid(result: Int, text: String? = null) {
        assertThat(resultCode).isEqualTo(result)

        if (text == null) return
        val resultText = resultData.getStringExtra(TextEditorActivity.Companion.EXTRA_TEXT)
        assertThat(resultText).isEqualTo(text)
    }

    @Test
    fun testValidEdit() = extension.use {
        waitForIdle()
        Espresso.onView(ViewMatchers.isAssignableFrom(EditText::class.java))
            .check(ViewAssertions.matches(ViewMatchers.withText(INPUT_TEXT)))
            .perform(ViewActions.replaceText(INPUT_URL), ViewActions.closeSoftKeyboard())

        waitForIdle()
        onNodeWithTag(EDITOR_APP_BAR_DONE_TEST_TAG)
            .assertIsEnabled()
            .performClick()

        extension.scenario.result.assertValid(Activity.RESULT_OK, INPUT_URL)
    }

    @Test
    fun testCancel() = extension.use {
        waitForIdle()
        Espresso.onView(ViewMatchers.isAssignableFrom(EditText::class.java))
            .check(ViewAssertions.matches(ViewMatchers.withText(INPUT_TEXT)))

        waitForIdle()
        onNodeWithTag(EDITOR_APP_BAR_CANCEL_TEST_TAG)
            .assertIsEnabled()
            .performClick()

        extension.scenario.result.assertValid(Activity.RESULT_CANCELED)
    }
}
