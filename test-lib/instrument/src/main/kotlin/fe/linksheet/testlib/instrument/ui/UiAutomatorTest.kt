package fe.linksheet.testlib.instrument.ui

import androidx.test.uiautomator.UiDevice
import fe.linksheet.testlib.instrument.InstrumentationTest

abstract class UiAutomatorTest : InstrumentationTest {
    val interactor = AppInteractor(targetContext)
    val device by lazy { UiDevice.getInstance(instrumentation) }

    val testApp by lazy { interactor.getTestAppInfo() }
}
