package fe.linksheet.debug.module.viewmodel

import androidx.lifecycle.ViewModel
import app.linksheet.feature.devicecompat.miui.MiuiCompatProvider
import fe.linksheet.debug.module.devicecompat.DebugMiuiCompatProvider
import fe.linksheet.debug.module.preference.DebugPreferenceRepository
import fe.linksheet.debug.module.preference.DebugPreferences

class DebugViewModel(
    miuiCompatProvider: MiuiCompatProvider,
    preferenceRepository: DebugPreferenceRepository
) : ViewModel() {
    val drawBorders = preferenceRepository.asViewModelState(DebugPreferences.drawBorders)
    val debugMiuiCompatProvider = miuiCompatProvider as? DebugMiuiCompatProvider

    fun toggleMiuiCompatRequired(): Boolean {
        return debugMiuiCompatProvider?.toggleRequired() == true
    }
}
