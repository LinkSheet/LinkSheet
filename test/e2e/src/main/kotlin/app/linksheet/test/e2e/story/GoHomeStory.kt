package app.linksheet.test.e2e.story

import androidx.test.uiautomator.UiAutomatorTestScope

object GoHomeStory {
    fun UiAutomatorTestScope.awaitHome() {
        pressHome()
        waitForAppToBeVisible(device.launcherPackageName)
    }
}
