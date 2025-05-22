package fe.linksheet.debug.module.debug

import androidx.compose.runtime.Composable
import fe.linksheet.debug.composable.DebugMenuSlot
import fe.linksheet.debug.module.viewmodel.DebugViewModel
import fe.linksheet.module.debug.DebugMenuSlotProvider

class RealDebugMenuSlotProvider(private val viewModel: DebugViewModel) : DebugMenuSlotProvider {
    override val enabled: Boolean = true

    @Composable
    override fun SlotContent(navigate: (String) -> Unit) {
        DebugMenuSlot(viewModel = viewModel, navigate = navigate)
    }
}
