package fe.linksheet.extension.compose

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ln

@Stable
fun Color.atElevation(
    surfaceTint: Color,
    elevation: Dp,
): Color {
    if (elevation == 0.dp) return this

    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return surfaceTint.copy(alpha = alpha).compositeOver(this)
}
