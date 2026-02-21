package app.linksheet.feature.libredirect.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.libredirect.LibRedirectData
import app.linksheet.feature.libredirect.viewmodel.LibRedirectSettingsViewModel
import fe.composekit.preference.fakeBooleanVM
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class LibRedirectSettingsRouteTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testStateRestoration() = runTest {
        val restorationTester = StateRestorationTester(rule)

        val services = (1..10).map {
            LibRedirectSettingsViewModel.LibRedirectServiceWithInstance(
                service = LibRedirectData.RedditService.run { copy(key = key + it, name = name + it) },
                enabled = true,
                instance = null
            )
        }

        restorationTester.setContent {
            LibRedirectSettingsRouteInternal(
                onBackPressed = {},
                navigate = { },
                enableLibRedirect = fakeBooleanVM(true),
                services = services
            )
        }

        rule.onRoot(true).printToLog(LibRedirectSettingsRouteTest::class.simpleName!!)
        rule.onNodeWithTag(LIBREDIRECT_SERVICES_LIST_TEST_TAG)
            .performTouchInput {
                swipe(
                    start = center,
                    end = Offset(center.x, center.y - 1000),
                    durationMillis = 200,
                )
            }

        rule.onNodeWithText("Reddit10").assertIsDisplayed()
        restorationTester.emulateSavedInstanceStateRestore()
        rule.onNodeWithText("Reddit10").assertIsDisplayed()
    }
}
