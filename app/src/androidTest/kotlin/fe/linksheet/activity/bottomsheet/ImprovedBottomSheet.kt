package fe.linksheet.activity.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import de.mannodermaus.junit5.compose.createAndroidComposeExtension
import fe.linksheet.activity.BaseComponentActivity
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalTestApi::class)
class ImprovedBottomSheet {
    @get:Rule
    val composeTestRule = createComposeRule()

    @JvmField @RegisterExtension
    val composeExtension = createAndroidComposeExtension<BaseComponentActivity>()

    @Test
    fun myTest() {
        composeExtension.use {

        }
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
