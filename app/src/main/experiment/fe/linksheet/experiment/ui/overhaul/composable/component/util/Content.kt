package fe.linksheet.experiment.ui.overhaul.composable.component.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

typealias Content = @Composable () -> Unit
typealias OptionalContent = Content?

@Composable
inline fun <T : Any?> rememberOptionalContent(key1: T?, crossinline content: @Composable (T) -> Unit): OptionalContent {
    return remember(key1 = key1) {
        key1?.let {
            { content(it) }
        }
    }
}

@Composable
inline fun rememberContent(crossinline content: @Composable () -> Unit): Content {
    return remember {
        { content() }
    }
}
