package fe.linksheet.composable.util

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import fe.linksheet.util.buildconfig.Build

fun Modifier.debugBorder(width: Dp, color: Color, shape: Shape = RectangleShape): Modifier {
    if(!Build.IsDebug) return this
    return border(width, SolidColor(color), shape)
}
