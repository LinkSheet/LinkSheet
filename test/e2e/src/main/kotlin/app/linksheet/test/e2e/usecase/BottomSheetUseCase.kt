package app.linksheet.test.e2e.usecase

import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiAutomatorTestScope
import androidx.test.uiautomator.textAsString
import app.linksheet.api.BOTTOM_SHEET_JUST_ONCE_TEST_TAG
import app.linksheet.api.BOTTOM_SHEET_TEST_TAG

object BottomSheetUseCase {
    fun UiAutomatorTestScope.expandSheet() {
        val bottomSheet = onElement { viewIdResourceName == BOTTOM_SHEET_TEST_TAG }
        bottomSheet.swipe(Direction.UP, 0.2f)
        waitForStableInActiveWindow()
    }
    fun UiAutomatorTestScope.openJustOnce(appLabel: String, appPackage: String) {
        val browserRow = onElement { textAsString() == appLabel }
        browserRow.click()

        val justOnceButton = onElement { viewIdResourceName == BOTTOM_SHEET_JUST_ONCE_TEST_TAG }
        justOnceButton.click()
        waitForAppToBeVisible(appPackage)
    }
}
