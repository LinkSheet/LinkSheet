package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@Composable
fun ColoredIcon(icon: ImageVector, @StringRes description: Int) {
    Image(
        imageVector = icon,
        contentDescription = stringResource(id = description),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
    )
}