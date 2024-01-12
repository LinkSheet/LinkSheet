package fe.linksheet.extension

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

@Composable
private fun <T> Flow<T>.collectOnIO(initialState: T): State<T> {
    return collectAsStateWithLifecycle(initialValue = initialState, context = Dispatchers.IO)
}

@Composable
fun <T> Flow<T>.collectOnIO(): State<T?> {
    return collectOnIO(null)
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun <T> StateFlow<T>.collectOnIO(): State<T> {
    return collectOnIO(value)
}

typealias Transform<T, R, S> = suspend (T, ProduceSideEffect<S>) -> R
typealias ProduceSideEffect<S> = (S) -> Unit
typealias SideEffectHandler<S> = suspend (List<S>) -> Unit

inline fun <T, R, S> Flow<T>.mapProducingSideEffect(
    crossinline transform: Transform<T, R, S>,
    crossinline handleSideEffects: SideEffectHandler<S>
): Flow<R> {
    val sideEffects = mutableListOf<S>()

    val transformed = transform { value ->
        val result = transform(value, sideEffects::add)
        return@transform emit(result)
    }

    return transformed.onCompletion { handleSideEffects(sideEffects) }
}
