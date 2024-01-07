package fe.linksheet.extension.compose

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

// https://stackoverflow.com/a/69061897
//data class LifecycleState(val lastState: Lifecycle.Event, val state: Lifecycle.Event)

//@Composable
//fun Lifecycle.observe(ignoreFirstEvent: Lifecycle.Event? = Lifecycle.Event.ON_RESUME): State<LifecycleState> {
//    var currentEvent by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
//    var lastEvent by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
//
//    val lifecycleState = remember {
//        derivedStateOf { LifecycleState(lastEvent, currentEvent) }
//    }
//
//    var isFirst by remember { mutableStateOf(true) }
//
//    DisposableEffect(this) {
//        val observer = LifecycleEventObserver { _, event ->
//            if (isFirst && event == ignoreFirstEvent) {
//                isFirst = false
//                return@LifecycleEventObserver
//            }
//
//            if (!isFirst || ignoreFirstEvent == null) {
//                lastEvent = currentEvent
//                currentEvent = event
//            }
//        }
//
//        this@observe.addObserver(observer)
//        onDispose {
//            this@observe.removeObserver(observer)
//        }
//    }
//
//    return lifecycleState
//}

@Composable
fun Lifecycle.observeFocusAsState(ignoreFirstEvent: Lifecycle.Event? = Lifecycle.Event.ON_RESUME): State<Lifecycle.Event> {
    val currentEvent = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    var lastEvent by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    var isFirst by remember { mutableStateOf(true) }

    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            if (isFirst && event == ignoreFirstEvent) {
                isFirst = false
                return@LifecycleEventObserver
            }

            if (!isFirst || ignoreFirstEvent == null) {
                lastEvent = currentEvent.value
                currentEvent.value = event
            }
        }

        this@observeFocusAsState.addObserver(observer)
        onDispose {
            this@observeFocusAsState.removeObserver(observer)
        }
    }

    return currentEvent
}


@Composable
fun Lifecycle.ObserveStateChange(
    invokeOnCall: Boolean = false,
    ignoreFirstEvent: Lifecycle.Event? = Lifecycle.Event.ON_RESUME,
    observeEvents: Set<Lifecycle.Event>? = setOf(Lifecycle.Event.ON_RESUME),
    onStateChange: (Lifecycle.Event?) -> Unit
) {
    val callback = rememberUpdatedState(onStateChange)
    if (invokeOnCall) {
        LaunchedEffect(Unit) {
            callback.value(null)
        }
    }

    val event by observeFocusAsState(ignoreFirstEvent)
    LaunchedEffect(event) {
        if (observeEvents == null || event in observeEvents) callback.value(event)
    }
}
