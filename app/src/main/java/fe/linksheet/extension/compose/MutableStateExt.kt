package fe.linksheet.extension.compose

import androidx.compose.runtime.MutableState

inline fun MutableState<Boolean>.updateState(
    crossinline update: (Boolean) -> Unit
): () -> Unit = {
    value = !value
    update(value)
}

inline fun MutableState<Boolean>.updateStateFromResult(
    crossinline update: (Boolean) -> Unit
): (Boolean) -> Unit = {
    value = it
    update(value)
}
