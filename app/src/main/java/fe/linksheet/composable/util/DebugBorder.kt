package fe.linksheet.composable.util

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import fe.linksheet.util.buildconfig.BuildType

fun Modifier.debugBorder(width: Dp, color: Color, shape: Shape = RectangleShape): Modifier {
    if(!BuildType.current.allowDebug) return this
    return border(width, SolidColor(color), shape)
}
