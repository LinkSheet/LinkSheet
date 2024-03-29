package fe.linksheet.experiment.ui.overhaul.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import fe.linksheet.ui.LightColors

@Composable
fun PreviewThemeNew(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = NewTypography,
        content = content
    )
}
