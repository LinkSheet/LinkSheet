package fe.linksheet.extension.compose

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.CoroutineScope

// https://stackoverflow.com/a/69061897
data class LifecycleState(val lastState: Lifecycle.Event, val state: Lifecycle.Event)

@Composable
fun Lifecycle.observeAsState(ignoreFirst: Lifecycle.Event? = Lifecycle.Event.ON_RESUME): LifecycleState {
    var state by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    var lastState by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    var isFirst by remember { mutableStateOf(true) }

    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            if (isFirst && event == ignoreFirst) {
                isFirst = false
                return@LifecycleEventObserver
            }

            if (!isFirst || ignoreFirst == null) {
                lastState = state
                state = event
            }
        }
        this@observeAsState.addObserver(observer)
        onDispose {
            this@observeAsState.removeObserver(observer)
        }
    }

    return LifecycleState(state, lastState)
}

@Composable
fun Lifecycle.onStateChange(
    fireOnFirst: Boolean = false,
    state: Set<Lifecycle.Event?> = setOf(Lifecycle.Event.ON_RESUME),
    block: suspend CoroutineScope.() -> Unit
) {
    if (fireOnFirst) {
        LaunchedEffect(Unit) { block() }
    }

    val lifecycleState = observeAsState()
    LaunchedEffect(lifecycleState.state) {
        if (lifecycleState.state in state) block()
    }
}
