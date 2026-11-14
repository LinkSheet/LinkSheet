package fe.linksheet.activity.bottomsheet.compat.m3fix

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import app.linksheet.m3sheet.androidx.compose.material3_1_5_0_rc01.SheetState
import app.linksheet.m3sheet.androidx.compose.material3_1_5_0_rc01.SheetValue
import app.linksheet.m3sheet.androidx.compose.material3_1_5_0_rc01.rememberModalBottomSheetState
import fe.linksheet.activity.bottomsheet.compat.CompatSheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberM3FixModalBottomSheetState(): M3FixSheetState {
    val state = rememberModalBottomSheetState()
    return M3FixSheetState(state)
}

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class M3FixSheetState(val state: SheetState) : CompatSheetState {
    override fun isAnimationRunning(): Boolean = state.isAnimationRunning
    override suspend fun expand(): Unit = state.expand()
    override suspend fun partialExpand(): Unit = state.partialExpand()
    override suspend fun hide(): Unit = state.hide()
    override fun isExpanded(): Boolean = state.currentValue == SheetValue.Expanded
}
