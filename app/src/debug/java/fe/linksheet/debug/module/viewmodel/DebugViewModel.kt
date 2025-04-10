package fe.linksheet.debug.module.viewmodel

import androidx.lifecycle.ViewModel
import fe.linksheet.debug.module.devicecompat.DebugMiuiCompatProvider
import fe.linksheet.debug.module.preference.DebugPreferenceRepository
import fe.linksheet.debug.module.preference.DebugPreferences
import fe.linksheet.module.devicecompat.miui.MiuiCompatProvider

class DebugViewModel(
    val miuiCompatProvider: MiuiCompatProvider,
    val preferenceRepository: DebugPreferenceRepository
) : ViewModel() {
    val drawBorders = preferenceRepository.asViewModelState(DebugPreferences.drawBorders)
    val debugMiuiCompatProvider = miuiCompatProvider as? DebugMiuiCompatProvider

    fun toggleMiuiCompatRequired(): Boolean {
        return debugMiuiCompatProvider?.toggleRequired() == true
    }
}
