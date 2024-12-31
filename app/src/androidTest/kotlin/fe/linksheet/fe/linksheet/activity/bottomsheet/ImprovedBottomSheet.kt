package fe.linksheet.fe.linksheet.activity.bottomsheet

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import app.linksheet.testing.ResolveInfoMocks
import app.linksheet.testing.ResolveInfoMocks.toDisplayActivityInfo
import fe.linksheet.activity.bottomsheet.content.success.AppContentRoot
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
        val apps = ResolveInfoMocks.allResolved.map { it.toDisplayActivityInfo() }

        AppContentRoot(
            gridLayout = false,
            apps = apps,
            uri = Uri.parse("https://www.youtube.com/watch?v=evIpx9Onc2c"),
            appListSelectedIdx = 1,
            hasPreferredApp = false,
            hideChoiceButtons = false,
            showPackage = false,
            isPrivateBrowser = { _, _ -> null },
            showToast = { _, _, _ -> },
            showNativeLabel = false,
            launch = { info, modifier ->
            },
            launch2 = { index, info, type, modifier ->
            }
        )
    }
}
