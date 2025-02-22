package app.linksheet.preview

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import fe.linksheet.composable.ui.LocalActivity
import fe.linksheet.composable.ui.PreviewTheme

@Composable
fun PreviewContainer(content: @Composable () -> Unit) {
    PreviewTheme {
        CompositionLocalProvider(LocalActivity provides Activity()) {
            Column() {
                content()
            }
        }
    }
}
