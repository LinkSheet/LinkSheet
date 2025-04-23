package fe.linksheet.composable.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashedBorderBox(
    text: AnnotatedString,
    fontSize: TextUnit = 10.sp,
    surface: Color,
    strokeWidth: Dp = 1.dp,
    color: Color = Color.Gray,
    cornerRadius: Dp = 12.dp,
    padding: Dp,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier = Modifier.dashedBorder(text, fontSize = fontSize, surface, strokeWidth, color, cornerRadius)) {
        Column(
            modifier = Modifier.padding(all = padding),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}

fun Modifier.dashedBorder(
    text: AnnotatedString,
    fontSize: TextUnit,
    surface: Color,
    strokeWidth: Dp,
    color: Color,
    cornerRadius: Dp,
) = composed(factory = {
    val density = LocalDensity.current
    val strokeWidthPx = density.run { strokeWidth.toPx() }
    val cornerRadiusPx = density.run { cornerRadius.toPx() }
    val textMeasurer = rememberTextMeasurer()

    then(
        Modifier.drawWithCache {
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

                val textLayoutResult = textMeasurer.measure(
                    text = text,
                    style = TextStyle(
                        color = color,
                        background = surface,
                        fontSize = fontSize
                    )
                )

                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        x = cornerRadiusPx,
                        y = -textLayoutResult.size.height / 2f
                    )
                )
            }
        }
    )
})

@Preview(showBackground = true, widthDp = 190, heightDp = 100)
@Composable
private fun DashedBorderPreview() {
    Box(modifier = Modifier.padding(all = 16.dp), contentAlignment = Alignment.CenterStart) {
        DashedBorderBox(
            text = AnnotatedString("Helo"),
            surface = MaterialTheme.colorScheme.surface,
            padding = 16.dp, strokeWidth = 1.dp, color = Color.Gray, cornerRadius = 12.dp
        ) {
            Text(text = "989980bb-e6fd-4388")
        }

    }
}
