package fe.linksheet.composable.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import fe.linksheet.extension.observeAsState
import kotlinx.coroutines.CoroutineScope

@Composable
fun LaunchedEffectOnFirstAndResume(block: suspend CoroutineScope.() -> Unit) {
    val lifecycleState = LocalLifecycleOwner.current.lifecycle
        .observeAsState(ignoreFirst = Lifecycle.Event.ON_RESUME)

    LaunchedEffect(Unit) {
        block()
    }

    LaunchedEffect(lifecycleState.first) {
        if (lifecycleState.first == Lifecycle.Event.ON_RESUME) {
            block()
        }
    }
}