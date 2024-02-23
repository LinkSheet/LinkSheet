package fe.linksheet.activity.bottomsheet.failure

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fe.linksheet.R

@Composable
fun ShareToButton(
    modifier: Modifier = Modifier,
    isTextBasedButton: Boolean,
    onClick: () -> Unit
) {
    if (!isTextBasedButton) {
        ElevatedButton(modifier = modifier, onClick = onClick) {
            ButtonContent(
                imageVector = Icons.Default.Share,
                text = R.string.send_to
            )
        }
    } else {
        TextButton(modifier = modifier, onClick = onClick) {
            ButtonContent(
                imageVector = Icons.Default.Share,
                text = R.string.send_to
            )
        }
    }
}
