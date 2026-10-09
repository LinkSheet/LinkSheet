package app.linksheet.test.e2e.dialog

import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.onElement
import androidx.test.uiautomator.onElementOrNull
import androidx.test.uiautomator.watcher.ScopedUiWatcher
import app.linksheet.api.ANALYTICS_DIALOG__SAVE_TEST_TAG

class AnalyticsDialog (val uiDevice: UiDevice) : ScopedUiWatcher<AnalyticsDialog.Scope> {

    override fun isVisible(): Boolean {
        return uiDevice.onElementOrNull(0) { viewIdResourceName == ANALYTICS_DIALOG__SAVE_TEST_TAG  } != null
    }

    override fun scope(): Scope = Scope(uiDevice)

    class Scope(val uiDevice: UiDevice) {

        fun clickSave() {
            uiDevice.onElement { viewIdResourceName == ANALYTICS_DIALOG__SAVE_TEST_TAG  }.click()
        }
    }
}
