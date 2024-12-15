package fe.linksheet.module.debug

import androidx.compose.runtime.Composable

interface DebugMenuSlotProvider {
    val enabled: Boolean

    @Composable
    fun SlotContent(navigate: (String) -> Unit)
}

object NoOpDebugMenuSlotProvider : DebugMenuSlotProvider {
    override val enabled: Boolean = false

    @Composable
    override fun SlotContent(navigate: (String) -> Unit) {
    }
}
