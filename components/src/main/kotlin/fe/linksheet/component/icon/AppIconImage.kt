package fe.linksheet.component.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import fe.linksheet.component.list.base.ShapeListItemDefaults

@Composable
fun AppIconImage(
    modifier: Modifier = ShapeListItemDefaults.BaseContentModifier,
    bitmap: ImageBitmap,
    label: String,
) {
    Image(
        bitmap = bitmap,
        contentDescription = label,
        modifier = modifier.size(32.dp)
    )
}
