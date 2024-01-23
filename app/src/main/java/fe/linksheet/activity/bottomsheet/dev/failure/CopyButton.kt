package fe.linksheet.activity.bottomsheet.dev.failure

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fe.linksheet.R

@Composable
private fun CopyButton(
    modifier: Modifier = Modifier,
    isTextBasedButton: Boolean,
    onClick: () -> Unit
) {
    if (!isTextBasedButton) {
        ElevatedButton(modifier = modifier, onClick = onClick) {
            ButtonContent(imageVector = Icons.Default.ContentCopy, text = R.string.copy_url)
        }
    } else {
        TextButton(modifier = modifier, onClick = onClick) {
            ButtonContent(imageVector = Icons.Default.ContentCopy, text = R.string.copy_url)
        }
    }
}



