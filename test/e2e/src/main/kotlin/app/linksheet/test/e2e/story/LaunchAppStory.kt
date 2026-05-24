package app.linksheet.test.e2e.story

import android.content.Intent
import androidx.test.uiautomator.UiAutomatorTestScope

object LaunchAppStory {
    fun UiAutomatorTestScope.launch(packageName: String) {
        startApp(packageName, listOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
        waitForAppToBeVisible(packageName)
    }
}
