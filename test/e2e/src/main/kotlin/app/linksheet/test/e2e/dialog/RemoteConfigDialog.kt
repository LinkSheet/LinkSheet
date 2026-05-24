package app.linksheet.test.e2e.dialog

import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.onElement
import androidx.test.uiautomator.onElementOrNull
import androidx.test.uiautomator.watcher.ScopedUiWatcher
import app.linksheet.api.REMOTE_CONFIG_DIALOG__ENABLE_TEST_TAG

class RemoteConfigDialog(val uiDevice: UiDevice) : ScopedUiWatcher<RemoteConfigDialog.Scope> {

    override fun isVisible(): Boolean {
        return uiDevice.onElementOrNull(0) { viewIdResourceName == REMOTE_CONFIG_DIALOG__ENABLE_TEST_TAG } != null
    }

    override fun scope(): Scope = Scope(uiDevice)

    class Scope(val uiDevice: UiDevice) {

        fun clickEnable() {
            uiDevice.onElement { viewIdResourceName == REMOTE_CONFIG_DIALOG__ENABLE_TEST_TAG }.click()
        }
    }
}
