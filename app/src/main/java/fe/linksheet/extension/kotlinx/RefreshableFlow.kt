package fe.linksheet.extension.kotlinx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T> RefreshableStateFlow(initial: T, block: suspend () -> T): RefreshableReadOnlyFlow<T> {
    val flow = MutableStateFlow(initial)
    return RefreshableReadOnlyFlow(block, flow)
}

fun <T> RefreshableReadOnlyFlow<T>.asStateFlow(): StateFlow<T> {
    return this
}

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
class RefreshableReadOnlyFlow<T>(
    private val block: suspend () -> T,
    private val flow: MutableStateFlow<T>,
) : StateFlow<T> by flow {

    suspend fun refresh() {
        flow.emit(block())
    }
}

@Composable
fun <T> RefreshableReadOnlyFlow<T>.collectRefreshableAsStateWithLifecycle(
    initialValue: T,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> = collectRefreshableAsStateWithLifecycle(
    initialValue = initialValue,
    lifecycle = lifecycleOwner.lifecycle,
    minActiveState = minActiveState,
    context = context
)

@Composable
fun <T> RefreshableReadOnlyFlow<T>.collectRefreshableAsStateWithLifecycle(
    initialValue: T,
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> {
    return produceState(initialValue, this, lifecycle, minActiveState, context) {
        lifecycle.repeatOnLifecycle(minActiveState) {
            if (context == EmptyCoroutineContext) {
                this@collectRefreshableAsStateWithLifecycle.refresh()
                this@collectRefreshableAsStateWithLifecycle.collect { this@produceState.value = it }
            } else
                withContext(context) {
                    this@collectRefreshableAsStateWithLifecycle.refresh()
                    this@collectRefreshableAsStateWithLifecycle.collect { this@produceState.value = it }
                }
        }
    }
}
