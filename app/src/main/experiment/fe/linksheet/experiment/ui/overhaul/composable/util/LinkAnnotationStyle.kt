package fe.linksheet.experiment.ui.overhaul.composable.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle

object LinkAnnotationStyleDefaults {
    val primary: LinkAnnotationStyle
        @Composable
        @ReadOnlyComposable get() = LinkAnnotationStyle(
            style = SpanStyle(color = MaterialTheme.colorScheme.primary)
        )
}

@Immutable
data class LinkAnnotationStyle(
    val style: SpanStyle,
    val focusedStyle: SpanStyle? = null,
    val hoveredStyle: SpanStyle? = null,
    val pressedStyle: SpanStyle? = null,
    val interactionListener: LinkInteractionListener? = null
) {
    fun toUrlAnnotation(value: String): LinkAnnotation.Url {
        return LinkAnnotation.Url(value, style, focusedStyle, hoveredStyle, pressedStyle, interactionListener)
    }
}
