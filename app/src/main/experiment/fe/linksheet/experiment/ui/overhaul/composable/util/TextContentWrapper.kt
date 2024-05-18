package fe.linksheet.experiment.ui.overhaul.composable.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember


@Composable
fun TextContentWrapper(textContent: TextContent) {
    val content = remember(textContent) { textContent.content }
    content()
}
