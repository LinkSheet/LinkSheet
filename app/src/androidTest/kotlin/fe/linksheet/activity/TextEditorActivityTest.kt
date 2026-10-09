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
import app.linksheet.testlib.koin.junit5.AutoCloseKoinTest
import app.linksheet.testlib.koin.junit5.KoinTestExtension
import assertk.assertThat
import assertk.assertions.isEqualTo
import de.mannodermaus.junit5.compose.createAndroidComposeExtension
import de.mannodermaus.junit5.extensions.GrantPermissionExtension
import fe.composekit.core.putEnumExtra
import fe.linksheet.Validator
import fe.linksheet.composable.page.edit.EDITOR_APP_BAR_CANCEL_TEST_TAG
import fe.linksheet.composable.page.edit.EDITOR_APP_BAR_DONE_TEST_TAG
import fe.linksheet.debug.module.TestRootModule
import fe.linksheet.testlib.core.ActivityInvoker
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalTestApi::class)
internal class TextEditorActivityTest : AutoCloseKoinTest() {
    @JvmField
    @RegisterExtension
    @Order(1)
    val koinTestExtension = KoinTestExtension.create {
        modules(TestRootModule)
    }

    @JvmField
    @RegisterExtension
    @Order(2)
    val grantPermissionExtension = GrantPermissionExtension.grant("android.permission.POST_NOTIFICATIONS")

    @JvmField
    @RegisterExtension
    @Order(3)
    val extension = createAndroidComposeExtension {
        launchActivityForResult(ActivityInvoker.getIntentForActivity<TextEditorActivity> {
            putExtra(TextEditorActivity.EXTRA_TEXT, INPUT_TEXT)
            putEnumExtra(
                TextEditorActivity.EXTRA_SOURCE,
                TextEditorActivity.ExtraSource.ClipboardCard
            )
            putEnumExtra(TextEditorActivity.EXTRA_VALIDATOR, Validator.WebUriTextValidator)
        })
    }

    private val INPUT_TEXT = "Hello World"
    private val INPUT_URL = "https://linksheet.app"

    private fun Instrumentation.ActivityResult.assertValid(result: Int, text: String? = null) {
        assertThat(resultCode).isEqualTo(result)

        if (text == null) return
        val resultText = resultData.getStringExtra(TextEditorActivity.EXTRA_TEXT)
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
