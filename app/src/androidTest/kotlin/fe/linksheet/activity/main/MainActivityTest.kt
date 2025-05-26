@file:OptIn(ExperimentalTestApi::class)

package fe.linksheet.activity.main

import android.content.Intent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.core.net.toUri
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import fe.linksheet.testlib.instrument.ui.UiTest
import fe.linksheet.testlib.instrument.ui.AppInteractor
import fe.linksheet.util.intent.buildIntent
import kotlin.test.Test

internal class MainActivityTest : UiTest {
    private val interactor = AppInteractor(device, targetContext)

    @Test
    fun test() {
        interactor.launch()
        interactor.dismissDialogs()
        interactor.setAsDefaultBrowser()
        interactor.stop()

        val intent = buildIntent(Intent.ACTION_VIEW, "https://linksheet.app".toUri())
        interactor.startApp(intent)

        device.wait(Until.hasObject(By.pkg(device.launcherPackageName).depth(0)), 10_000)
    }
}
