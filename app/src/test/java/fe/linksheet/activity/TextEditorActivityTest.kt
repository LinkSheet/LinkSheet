package fe.linksheet.activity

import android.app.Activity
import android.app.Instrumentation
import android.os.Build
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
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.composekit.core.putEnumExtra
import fe.linksheet.composable.page.edit.EDITOR_APP_BAR_CANCEL_TEST_TAG
import fe.linksheet.composable.page.edit.EDITOR_APP_BAR_DONE_TEST_TAG
import fe.linksheet.testlib.core.ActivityInvoker
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class TextEditorActivityTest : BaseUnitTest {
    companion object {
        private const val INPUT_TEXT = "Hello World"
        private const val INPUT_URL = "https://linksheet.app"
    }

    private val intent = ActivityInvoker.getIntentForActivity<TextEditorActivity> {
        putExtra(TextEditorActivity.EXTRA_TEXT, INPUT_TEXT)
        putEnumExtra(TextEditorActivity.EXTRA_SOURCE, TextEditorActivity.ExtraSource.ClipboardCard)
        putEnumExtra(TextEditorActivity.EXTRA_VALIDATOR, TextEditorActivity.ExtraValidator.WebUriTextValidator)
    }

    private fun Instrumentation.ActivityResult.assertValid(result: Int, text: String? = null) {
        assertThat(resultCode).isEqualTo(result)

        if (text == null) return
        val resultText = resultData.getStringExtra(TextEditorActivity.EXTRA_TEXT)
        assertThat(resultText).isEqualTo(text)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testValidEdit() {
//        runAndroidComposeUiTest(activityLauncher = { launchActivityForResult<TextEditorActivity>(intent) }) { scenario ->
//            waitForIdle()
//            Espresso.onView(ViewMatchers.isAssignableFrom(EditText::class.java))
//                .check(ViewAssertions.matches(ViewMatchers.withText(INPUT_TEXT)))
//                .perform(ViewActions.replaceText(INPUT_URL), ViewActions.closeSoftKeyboard())
//
//            waitForIdle()
//            onNodeWithTag(EDITOR_APP_BAR_DONE_TEST_TAG)
//                .assertIsEnabled()
//                .performClick()
//
//            scenario.result.assertValid(Activity.RESULT_OK, INPUT_URL)
//        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testCancel() {
//        runAndroidComposeUiTest(activityLauncher = { launchActivityForResult<TextEditorActivity>(intent) }) { scenario ->
//            waitForIdle()
//            Espresso.onView(ViewMatchers.isAssignableFrom(EditText::class.java))
//                .check(ViewAssertions.matches(ViewMatchers.withText(INPUT_TEXT)))
//
//            waitForIdle()
//            onNodeWithTag(EDITOR_APP_BAR_CANCEL_TEST_TAG)
//                .assertIsEnabled()
//                .performClick()
//
//            scenario.result.assertValid(Activity.RESULT_CANCELED)
//        }
    }
}
