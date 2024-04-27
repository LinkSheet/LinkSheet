package fe.linksheet.experiment.ui.overhaul.composable.component.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun AppIconImage(
    bitmap: ImageBitmap,
    label: String,
) {
    Image(
        bitmap = bitmap,
        contentDescription = label,
        modifier = Modifier.size(32.dp)
    )
}
