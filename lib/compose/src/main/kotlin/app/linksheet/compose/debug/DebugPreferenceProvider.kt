package app.linksheet.compose.debug

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface DebugPreferenceProvider {
    val drawBorders: StateFlow<Boolean>
    val bottomSheetLog: StateFlow<Boolean>
    val m3Log: StateFlow<Boolean>
}

object NoOpDebugPreferenceProvider : DebugPreferenceProvider {
    override val drawBorders = MutableStateFlow(false)
    override val bottomSheetLog = MutableStateFlow(false)
    override val m3Log = MutableStateFlow(false)
}

val LocalUiDebug: ProvidableCompositionLocal<DebugPreferenceProvider> = staticCompositionLocalOf {
    NoOpDebugPreferenceProvider
}
