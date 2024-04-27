package fe.linksheet.experiment.ui.overhaul.composable.component.icon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

object IconDefaults {
    val ContainerSize = 40.0.dp
}

@Composable
fun FilledIcon(
    enabled: Boolean = true,
    shape: Shape = IconButtonDefaults.filledShape,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    imageVector: ImageVector,
    contentDescription: String?,
) {
    Surface(
        shape = shape,
        color = colors.containerColor(enabled),
        contentColor = colors.contentColor(enabled),
    ) {
        Box(modifier = Modifier.size(IconDefaults.ContainerSize), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription
            )
        }
    }
}

@Stable
private fun IconButtonColors.containerColor(enabled: Boolean): Color =
    if (enabled) containerColor else disabledContainerColor

@Stable
internal fun IconButtonColors.contentColor(enabled: Boolean): Color =
    if (enabled) contentColor else disabledContentColor
