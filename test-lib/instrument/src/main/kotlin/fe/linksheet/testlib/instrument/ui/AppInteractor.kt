package fe.linksheet.testlib.instrument.ui

import android.content.Context
import android.content.Intent
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import fe.linksheet.testlib.instrument.LONG_TIMEOUT
import fe.linksheet.testlib.instrument.extension.findObjectWithTimeoutOrNull

class AppInteractor(
    private val device: UiDevice,
    private val targetContext: Context,
) {
    val targetPackageName = targetContext.packageName
    private val targetLaunchIntent = targetContext.packageManager.getLaunchIntentForPackage(targetPackageName)

    fun launch() {
        awaitHome()
        startApp(targetLaunchIntent)
    }

    fun awaitHome() {
        device.pressHome()
        awaitApp(device.launcherPackageName)
    }

    fun startApp(intent: Intent?) {
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        targetContext.startActivity(intent)
        awaitApp(targetPackageName)
    }

    fun awaitApp(packageName: String, timeout: Long = LONG_TIMEOUT) {
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), timeout)
    }

    private val defaultBrowserTexts = arrayOf("Set as default browser", "LinkSheet Debug", "Set as default")
    private val dismissDialogTexts = arrayOf("Enable", "Save")

    fun setAsDefaultBrowser() {
        for (text in defaultBrowserTexts) {
            device.findObjectWithTimeoutOrNull(By.text(text))?.click()
        }
    }

    fun dismissDialogs() {
        for (text in dismissDialogTexts) {
            device.findObjectWithTimeoutOrNull(By.text(text))?.click()
        }
    }

    fun stop() {
        awaitHome()
//        device.performActionAndWait(
//            { device.executeShellCommand("am force-stop $targetPackageName") },
//            Until.newWindow(),
//            1000
//        )
    }
}
