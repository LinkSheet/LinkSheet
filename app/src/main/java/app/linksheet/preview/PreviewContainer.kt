package app.linksheet.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import fe.linksheet.composable.ui.PreviewTheme

@Composable
fun PreviewContainer(content: @Composable () -> Unit) {
    PreviewTheme {
        Column() {
            content()
        }
    }
}
