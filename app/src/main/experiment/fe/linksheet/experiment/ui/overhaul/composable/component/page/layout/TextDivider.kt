package fe.linksheet.experiment.ui.overhaul.composable.component.page.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TextDivider(text: String, padding: PaddingValues = SaneLazyColumnPageDefaults.TextDividerPadding) {
    Text(
        modifier = Modifier.padding(padding),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleSmall
    )
}
