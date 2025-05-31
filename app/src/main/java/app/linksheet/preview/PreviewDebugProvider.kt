package app.linksheet.preview

import fe.linksheet.module.debug.DebugPreferenceProvider
import kotlinx.coroutines.flow.MutableStateFlow

fun PreviewDebugProvider(drawBorders: Boolean = true): DebugPreferenceProvider {
    return object : DebugPreferenceProvider {
        override val drawBorders = MutableStateFlow(drawBorders)
    }
}
