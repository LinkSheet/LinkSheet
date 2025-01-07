package fe.linksheet.activity.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class ImprovedBottomSheet {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun myTest() {
        composeTestRule.setContent {
            AppContentRootTest()
        }

        composeTestRule.waitForIdle()
        Thread.sleep(50_000)
    }

    @Composable
    fun AppContentRootTest() {

    }
}
