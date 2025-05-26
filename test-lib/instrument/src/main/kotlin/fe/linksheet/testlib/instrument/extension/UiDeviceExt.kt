package fe.linksheet.testlib.instrument.extension

import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import fe.linksheet.testlib.instrument.DEFAULT_TIMEOUT

fun UiDevice.findObjectWithTimeoutOrNull(
    selector: BySelector,
    timeout: Long = DEFAULT_TIMEOUT,
): UiObject2? {
    wait(Until.hasObject(selector), timeout)
    return findObject(selector)
}

fun UiDevice.findObjectWithTimeout(
    selector: BySelector,
    timeout: Long = DEFAULT_TIMEOUT,
): UiObject2 {
    return findObjectWithTimeoutOrNull(selector, timeout) ?: error("Failed to find '$selector'")
}
