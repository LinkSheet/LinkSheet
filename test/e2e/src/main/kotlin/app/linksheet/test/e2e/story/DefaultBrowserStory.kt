package app.linksheet.test.e2e.story

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.test.uiautomator.UiAutomatorTestScope
import androidx.test.uiautomator.textAsString

object DefaultBrowserStory {
    private val PACKAGE_NAMES = setOf(
        "com.google.android.permissioncontroller",
        "com.android.permissioncontroller"
    )

    private fun Context.findPackage(): ApplicationInfo? {
        for (pkg in PACKAGE_NAMES) {
            val appInfo = runCatching {
                packageManager.getApplicationInfo(pkg, PackageManager.MATCH_ALL)
            }.getOrNull()
            if (appInfo != null) {
                return appInfo
            }
        }
        return null
    }

    fun UiAutomatorTestScope.setAsDefaultBrowser(appLabel: String) {
        val appInfo = instrumentation.targetContext.findPackage() ?: error("No permission controller found!")
        device.executeShellCommand("am force-stop ${appInfo.packageName}")
        startActivityIntent(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
        waitForAppToBeVisible(appInfo.packageName)
        onElement { textAsString() == "Browser app" }.click()
        onElement { textAsString() == appLabel }.click()
    }
}
