package fe.linksheet.debug.module.viewmodel

import androidx.lifecycle.ViewModel
import fe.linksheet.debug.module.devicecompat.DebugMiuiCompatProvider
import fe.linksheet.module.devicecompat.MiuiCompatProvider

class DebugViewModel(
    val miuiCompatProvider: MiuiCompatProvider,
) : ViewModel() {
    val debugMiuiCompatProvider = miuiCompatProvider as? DebugMiuiCompatProvider

    fun toggleMiuiCompatRequired(): Boolean {
        return debugMiuiCompatProvider?.toggleRequired() == true
    }
}
