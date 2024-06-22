package fe.linksheet.component.util

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier


@Composable
fun TextContentWrapper(modifier: Modifier = Modifier, textContent: TextContent) {
    val content = remember(textContent) { textContent.content }

    Box(modifier = modifier) {
        content()
    }
}
