package fe.linksheet.experiment.ui.overhaul.composable.util

import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Immutable
data class TextOptions(
    val maxLines: Int = Int.MAX_VALUE,
    val overflow: TextOverflow = TextOverflow.Clip,
    val style: TextStyle? = null
)

val DefaultTextOptions = TextOptions()
val LocalTextOptions = compositionLocalOf(structuralEqualityPolicy()) { DefaultTextOptions }

typealias OptionalTextContent = TextContent?

@Composable
fun ProvideTextOptions(
    textOptions: TextOptions? = null,
    content: @Composable () -> Unit,
) {
    val options = textOptions ?: LocalTextOptions.current
    CompositionLocalProvider(LocalTextOptions provides options, content = content)
}
