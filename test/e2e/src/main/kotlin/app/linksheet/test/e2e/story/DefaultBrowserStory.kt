package app.linksheet.test.e2e.story

import android.content.Intent
import android.provider.Settings
import androidx.test.uiautomator.UiAutomatorTestScope
import androidx.test.uiautomator.textAsString

object DefaultBrowserStory {
    private const val PACKAGE_NAME: String = "com.google.android.permissioncontroller"

    fun UiAutomatorTestScope.setAsDefaultBrowser(appLabel: String) {
        device.executeShellCommand("am force-stop $PACKAGE_NAME")
        startActivityIntent(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
        waitForAppToBeVisible(PACKAGE_NAME)
        onElement { textAsString() == "Browser app" }.click()
        onElement { textAsString() == appLabel }.click()
    }
}
