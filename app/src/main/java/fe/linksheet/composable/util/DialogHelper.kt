package fe.linksheet.composable.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

data class StateDialogConfig<T>(
    val open: (T) -> Unit,
    val close: () -> Unit
)

data class DialogConfig(
    val open: () -> Unit,
    val close: () -> Unit
)

typealias OnClose<T> = (T) -> Unit

@Composable
fun <T, R, S> dialogHelper(
    fetch: suspend (T) -> R,
    onClose: OnClose<S?> = {},
    awaitFetchBeforeOpen: Boolean = false,
    notifyCloseNoState: Boolean = false,
    dynamicHeight: Boolean = false,
    content: @Composable (R?, OnClose<S?>) -> Unit,
): StateDialogConfig<T> {
    val open = remember { mutableStateOf(false) }
    val data = remember { mutableStateOf<T?>(null) }

    val config = remember {
        StateDialogConfig<T>(
            open = {
                if (!open.value) {
                    data.value = it
                    open.value = true
                }
            },
            close = {
                open.value = false
                data.value = null
            }
        )
    }

    if (open.value && data.value != null) {
        Dialog<T, R, S>(
            inputData = data.value!!,
            fetcher = fetch,
            onClose = { closeState ->
                open.value = false
                data.value = null
                if (notifyCloseNoState || closeState != null) {
                    onClose(closeState)
                }
            },
            awaitFetch = awaitFetchBeforeOpen,
            dynamicHeight = dynamicHeight,
            content = content
        )
    }

    return config
}

@Composable
fun <T, S> dialogHelper(
    state: T,
    onClose: OnClose<S?> = {},
    notifyCloseNoState: Boolean = false,
    dynamicHeight: Boolean = false,
    content: @Composable (T, OnClose<S?>) -> Unit,
): DialogConfig {
    val open = remember { mutableStateOf(false) }

    val config = remember {
        DialogConfig(
            open = {
                if (!open.value) {
                    open.value = true
                }
            },
            close = {
                open.value = false
            }
        )
    }

    if (open.value) {
        Dialog<T, S>(
            state = state,
            onClose = { closeState ->
                open.value = false
                if (notifyCloseNoState || closeState != null) {
                    onClose(closeState)
                }
            },
            dynamicHeight = dynamicHeight,
            content = content
        )
    }

    return config
}


@Composable
private fun <T, S> Dialog(
    state: T,
    onClose: OnClose<S?>,
    dynamicHeight: Boolean,
    content: @Composable (T, OnClose<S?>) -> Unit,
) {
    Dialog(
        dynamicHeight = dynamicHeight,
        onDismissRequest = { onClose(null) }
    ) {
        content(state, onClose)
    }
}

@Composable
private fun <T, R, S> Dialog(
    inputData: T,
    fetcher: suspend (T) -> R,
    onClose: OnClose<S?>,
    awaitFetch: Boolean,
    dynamicHeight: Boolean,
    content: @Composable (R?, OnClose<S?>) -> Unit,
) {
    var fetchedState by remember { mutableStateOf<R?>(null) }

    LaunchedEffect(Unit) {
        fetchedState = fetcher(inputData)
    }

    if (!awaitFetch || fetchedState != null) {
        Dialog(
            dynamicHeight = dynamicHeight,
            onDismissRequest = { onClose(null) }
        ) {
            content(fetchedState, onClose)
        }
    }
}