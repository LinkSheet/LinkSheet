package app.linksheet.compose.preview

import app.linksheet.compose.debug.DebugPreferenceProvider
import kotlinx.coroutines.flow.MutableStateFlow

@Suppress("FunctionName")
fun PreviewDebugProvider(drawBorders: Boolean = true): DebugPreferenceProvider {
    return object : DebugPreferenceProvider {
        override val drawBorders = MutableStateFlow(drawBorders)
        override val bottomSheetLog = MutableStateFlow(false)
        override val m3Log = MutableStateFlow(false)
    }
}
