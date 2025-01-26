package fe.linksheet.activity

import android.app.Activity
import android.app.Instrumentation
import android.widget.EditText
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.launchActivityForResult
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.linksheet.util.ActivityInvoker
import fe.linksheet.util.intent.putEnumExtra
import fe.linksheet.util.runAndroidComposeUiTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class TextEditorActivityTest {

    private val intent = ActivityInvoker.getIntentForActivity<TextEditorActivity> {
        putExtra(TextEditorActivity.EXTRA_TEXT, "hello world")
        putEnumExtra(TextEditorActivity.EXTRA_SOURCE, TextEditorActivity.ExtraSource.ClipboardCard)
        putEnumExtra(TextEditorActivity.EXTRA_VALIDATOR, TextEditorActivity.ExtraValidator.WebUriTextValidator)
    }

    private fun Instrumentation.ActivityResult.assertValid(result: Int, text: String? = null) {
        assertThat(resultCode).isEqualTo(result)

        if(text != null) {
            val resultText = resultData.getStringExtra(TextEditorActivity.EXTRA_TEXT)
            assertThat(resultText).isEqualTo(text)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test__valid_edit() {
        runAndroidComposeUiTest<TextEditorActivity>(
            activityLauncher = { launchActivityForResult<TextEditorActivity>(intent) }
        ) { scenario ->
            waitForIdle()
            onView(isAssignableFrom(EditText::class.java))
                .check(matches(withText("hello world")))
                .perform(replaceText("https://foobar.com"), closeSoftKeyboard())

            waitForIdle()
            onNodeWithTag("done")
                .assertIsEnabled()
                .performClick()

            scenario.result.assertValid(Activity.RESULT_OK, "https://foobar.com")
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test__cancel() {
        runAndroidComposeUiTest<TextEditorActivity>(
            activityLauncher = { launchActivityForResult<TextEditorActivity>(intent) }
        ) { scenario ->
            waitForIdle()
            onView(isAssignableFrom(EditText::class.java))
                .check(matches(withText("hello world")))

            waitForIdle()
            onNodeWithTag("cancel")
                .assertIsEnabled()
                .performClick()

            scenario.result.assertValid(Activity.RESULT_CANCELED)
        }
    }
}
