package app.linksheet.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import fe.android.compose.text.TextContent
import fe.android.compose.text.TextOptions

@Composable
fun InfoText(textContent: TextContent) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)

        TextContent(
            textContent = textContent,
            textOptions = TextOptions(style = MaterialTheme.typography.bodySmall),
        )
    }
}
