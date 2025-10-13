package app.linksheet.compose.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@Composable
fun PreviewContainer(content: @Composable () -> Unit) {
    PreviewTheme {
        Column() {
            content()
        }
    }
}
