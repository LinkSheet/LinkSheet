package app.linksheet.test.e2e.story

import androidx.test.uiautomator.UiAutomatorTestScope
import androidx.test.uiautomator.textAsString

object ChromeSetupStory {
    fun UiAutomatorTestScope.maybeDismissSetup() {
        val needsSetup = onElementOrNull { textAsString() == "Use without an account" } ?: return
        needsSetup.click()
        onElement { textAsString() == "Chrome notifications make things easier" }
        onElement { textAsString() == "No thanks" }.click()
    }
}
