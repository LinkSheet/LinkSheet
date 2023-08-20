package fe.linksheet.composable.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceSubtitle(
    modifier: Modifier = Modifier,
    text: String,
    paddingHorizontal: Dp = 10.dp,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Text(
        modifier = modifier.fillMaxWidth().padding(
                start = paddingHorizontal,
                top = 24.dp,
                bottom = 12.dp,
                end = paddingHorizontal
        ),
        text = text,
        color = color,
        style = MaterialTheme.typography.labelLarge
    )
}