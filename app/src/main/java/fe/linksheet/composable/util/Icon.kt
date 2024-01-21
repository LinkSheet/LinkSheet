package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@Deprecated("Use Icon instead", replaceWith = ReplaceWith("Icon"))
@Composable
fun ColoredIcon(
    icon: ImageVector,
    @StringRes descriptionId: Int,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Image(
        imageVector = icon,
        contentDescription = stringResource(id = descriptionId),
        colorFilter = ColorFilter.tint(color)
    )
}
