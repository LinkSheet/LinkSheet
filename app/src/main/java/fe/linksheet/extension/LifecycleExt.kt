package fe.linksheet.extension

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import fe.linksheet.util.to

// https://stackoverflow.com/a/69061897
@Composable
fun Lifecycle.observeAsState(ignoreFirst: Lifecycle.Event? = null): Pair<Lifecycle.Event, Lifecycle.Event> {
    var state by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    var lastState by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    var isFirst by remember { mutableStateOf(true) }

    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            if(isFirst && event == ignoreFirst){
                isFirst = false
                return@LifecycleEventObserver
            }

            if(!isFirst || ignoreFirst == null){
                lastState = state
                state = event
            }
        }
        this@observeAsState.addObserver(observer)
        onDispose {
            this@observeAsState.removeObserver(observer)
        }
    }

    return state to lastState
}
