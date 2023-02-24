package fe.linksheet.extension

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

// https://stackoverflow.com/a/69061897
@Composable
fun Lifecycle.observeAsState(): Pair<Lifecycle.Event, Lifecycle.Event> {
    var state by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    var lastState by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }

    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            lastState = state
            state = event
        }
        this@observeAsState.addObserver(observer)
        onDispose {
            this@observeAsState.removeObserver(observer)
        }
    }

    return state to lastState
}
