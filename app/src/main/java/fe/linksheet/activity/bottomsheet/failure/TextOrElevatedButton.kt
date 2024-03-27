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
    imageVector: ImageVector,
    @StringRes text: Int,
    onClick: () -> Unit,
) {
    ElevatedButton(modifier = modifier, onClick = onClick) {
        ButtonContent(imageVector = imageVector, text = text)
    }

}
