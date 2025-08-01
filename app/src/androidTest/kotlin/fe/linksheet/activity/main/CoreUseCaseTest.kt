package fe.linksheet.activity.main

import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.net.toUri
import androidx.test.uiautomator.By
import fe.composekit.intent.buildIntent
import fe.linksheet.testlib.instrument.extension.findObjectWithTimeoutOrNull
import fe.linksheet.testlib.instrument.ui.AppInteractor
import fe.linksheet.testlib.instrument.ui.UiAutomatorTest
import org.junit.jupiter.api.Test


internal class CoreUseCaseTest : UiAutomatorTest {
    private val interactor = AppInteractor(device, targetContext)

    data class DefaultBrowser(val name: String, val packageName: String)

    private fun getDefaultBrowser(): DefaultBrowser? {
        val defaultBrowserIntent = buildIntent(Intent.ACTION_VIEW, "http://".toUri())
        val activityInfo = targetContext.packageManager
            .resolveActivity(defaultBrowserIntent, PackageManager.MATCH_DEFAULT_ONLY)
            ?.activityInfo
            ?: return null
        val appName = activityInfo.applicationInfo?.name ?: return null
        val packageName = activityInfo.packageName ?: return null
        return DefaultBrowser(appName, packageName)
    }

    @Test
    fun test() {
        val regularDefaultBrowser = getDefaultBrowser()
//        assertThat(regularDefaultBrowser).isNotNull()

        interactor.launch()
        interactor.dismissDialogs()
        interactor.setAsDefaultBrowser()
        interactor.stop()

        Log.d("CoreUseCaseTest", getDefaultBrowser().toString())

//        assertThat(getDefaultBrowser())
//            .isNotNull()
//            .prop(DefaultBrowser::packageName)
//            .isEqualTo(interactor.targetPackageName)

        val intent = buildIntent(Intent.ACTION_VIEW, "https://linksheet.app".toUri())
        interactor.startApp(intent)

        if(regularDefaultBrowser != null) {
            device.findObjectWithTimeoutOrNull(By.text(regularDefaultBrowser.name))?.click()
            interactor.awaitApp(regularDefaultBrowser.packageName)
        }
    }
}
