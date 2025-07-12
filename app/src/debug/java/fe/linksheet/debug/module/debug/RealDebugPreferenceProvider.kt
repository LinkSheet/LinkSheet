package fe.linksheet.debug.module.debug

import fe.linksheet.debug.module.preference.DebugPreferenceRepository
import fe.linksheet.debug.module.preference.DebugPreferences
import fe.linksheet.module.debug.DebugPreferenceProvider

class RealDebugPreferenceProvider(val repository: DebugPreferenceRepository) : DebugPreferenceProvider {
    override val drawBorders = repository.asViewModelState(DebugPreferences.drawBorders).stateFlow
    override val bottomSheetLog = repository.asViewModelState(DebugPreferences.bottomSheetLog).stateFlow

    init {
    }
}
