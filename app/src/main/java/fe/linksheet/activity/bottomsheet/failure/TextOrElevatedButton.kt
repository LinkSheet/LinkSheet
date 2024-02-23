package fe.linksheet.activity.bottomsheet.failure

import androidx.annotation.StringRes
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun TextOrElevatedButton(
    modifier: Modifier = Modifier,
    isTextBasedButton: Boolean,
    imageVector: ImageVector,
    @StringRes text: Int,
    onClick: () -> Unit
) {
    if (!isTextBasedButton) {
        ElevatedButton(modifier = modifier, onClick = onClick) {
            ButtonContent(imageVector = imageVector, text = text)
        }
    } else {
        TextButton(modifier = modifier, onClick = onClick) {
            ButtonContent(imageVector = imageVector, text = text)
        }
    }
}
