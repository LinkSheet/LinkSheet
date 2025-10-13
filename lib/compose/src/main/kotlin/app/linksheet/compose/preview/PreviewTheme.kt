package app.linksheet.compose.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import app.linksheet.compose.theme.LightColors
import app.linksheet.compose.theme.NewTypography

@Composable
fun PreviewTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = NewTypography,
        content = content
    )
}
