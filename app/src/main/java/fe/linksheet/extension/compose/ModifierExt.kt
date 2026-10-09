package fe.linksheet.extension.compose

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

fun Modifier.dashedBorder(strokeWidth: Dp, color: Color, cornerRadiusDp: Dp): Modifier {
    return this.then(
        Modifier.drawWithCache {
            val strokeWidthPx = density.run { strokeWidth.toPx() }
            val cornerRadiusPx = density.run { cornerRadiusDp.toPx() }

            onDrawBehind {
                val stroke = Stroke(
                    width = strokeWidthPx,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                drawRoundRect(
                    color = color,
                    style = stroke,
                    cornerRadius = CornerRadius(cornerRadiusPx)
                )
            }
        }
    )
}
