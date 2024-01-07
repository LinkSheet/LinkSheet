package fe.linksheet.extension.compose

import androidx.compose.runtime.*
import androidx.compose.ui.platform.WindowInfo
import kotlinx.coroutines.flow.Flow

private fun WindowInfo.flowOfIsWindowFocused(): Flow<Boolean> {
    return snapshotFlow { isWindowFocused }
}

private suspend inline fun <T> Flow<T>.collect(set: Set<T>?, crossinline handle: (T) -> Unit) {
    collect {
        if (set == null || it in set) {
            handle(it)
        }
    }
}

@Composable
fun WindowInfo.observeFocusAsState(observe: Set<Boolean>? = setOf(true)): State<Boolean> {
    val focused = remember { mutableStateOf(isWindowFocused) }
    LaunchedEffect(this) {
        flowOfIsWindowFocused().collect(observe) { focused.value = it }
    }

    return focused
}

@Composable
private fun WindowInfo.ObserveFocusChangedInternal(observe: Set<Boolean>?, handle: () -> Unit) {
    val callback = rememberUpdatedState(handle)
    LaunchedEffect(this) {
        flowOfIsWindowFocused().collect(observe) { callback.value() }
    }
}

@Composable
fun WindowInfo.ObserveFocusChanged(observe: Set<Boolean>? = setOf(true, false), onFocusChanged: (Boolean) -> Unit) {
    ObserveFocusChangedInternal(observe) { onFocusChanged(isWindowFocused) }
}

@Composable
fun WindowInfo.OnFocused(handle: () -> Unit) {
    ObserveFocusChangedInternal(setOf(true)) { handle() }
}
