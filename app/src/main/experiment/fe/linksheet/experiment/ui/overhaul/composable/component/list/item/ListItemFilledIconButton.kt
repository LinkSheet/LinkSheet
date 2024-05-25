package fe.linksheet.experiment.ui.overhaul.composable.component.list.item

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import fe.linksheet.experiment.ui.overhaul.composable.component.list.base.ShapeListItemDefaults

@Composable
fun ListItemFilledIconButton(
    imageVector: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit,
) {
    Box(modifier = ShapeListItemDefaults.BaseContentModifier, contentAlignment = Alignment.Center) {
        FilledTonalIconButton(onClick = onClick) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription
            )
        }
    }
}
