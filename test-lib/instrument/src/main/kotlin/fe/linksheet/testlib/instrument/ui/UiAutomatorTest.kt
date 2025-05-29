package fe.linksheet.testlib.instrument.ui

import androidx.test.uiautomator.UiDevice
import fe.linksheet.testlib.instrument.InstrumentationTest

interface UiAutomatorTest : InstrumentationTest {
    val device: UiDevice
        get() = UiDevice.getInstance(instrumentation)
}
