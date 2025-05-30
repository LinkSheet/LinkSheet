@file:OptIn(ExperimentalTestApi::class)

package fe.linksheet.activity.main

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.core.net.toUri
import androidx.test.uiautomator.By
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.prop
import fe.linksheet.testlib.instrument.extension.findObjectWithTimeoutOrNull
import fe.linksheet.testlib.instrument.ui.AppInteractor
import fe.linksheet.testlib.instrument.ui.UiAutomatorTest
import fe.linksheet.util.intent.buildIntent
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
        val appName = activityInfo.applicationInfo.name
        val packageName = activityInfo.packageName
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

        assertThat(getDefaultBrowser())
            .isNotNull()
            .prop(DefaultBrowser::packageName)
            .isEqualTo(interactor.targetPackageName)

        val intent = buildIntent(Intent.ACTION_VIEW, "https://linksheet.app".toUri())
        interactor.startApp(intent)

        if(regularDefaultBrowser != null) {
            device.findObjectWithTimeoutOrNull(By.text(regularDefaultBrowser!!.name))?.click()
            interactor.awaitApp(regularDefaultBrowser.packageName)
        }
    }
}
