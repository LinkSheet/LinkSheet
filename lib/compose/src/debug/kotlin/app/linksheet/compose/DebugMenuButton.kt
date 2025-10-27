package app.linksheet.compose

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun DebugMenuButton(text: String, onClick: () -> Unit) {
    FilledTonalButton(
        colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        onClick = onClick
    ) {
        Text(text = text)
    }
}
