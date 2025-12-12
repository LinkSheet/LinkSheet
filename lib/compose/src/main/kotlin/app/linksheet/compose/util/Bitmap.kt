package app.linksheet.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun drawBitmap(
    size: Size,
    block: DrawScope.() -> Unit,
): ImageBitmap {
    return drawBitmap(size, LocalDensity.current, LocalLayoutDirection.current, block)
}

fun drawBitmap(
    size: Size,
    density: Density,
    layoutDirection: LayoutDirection,
    block: DrawScope.() -> Unit,
): ImageBitmap {
    val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
    CanvasDrawScope().draw(density, layoutDirection, Canvas(bitmap), size, block)
    return bitmap
}
