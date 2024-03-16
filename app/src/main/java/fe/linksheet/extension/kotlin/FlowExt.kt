package fe.linksheet.extension.kotlin

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.transform

@Composable
fun <T> Flow<T>.collectOnIO(initialState: T): State<T> {
    return collectAsStateWithLifecycle(initialValue = initialState, context = Dispatchers.IO)
}

@Composable
fun <T> Flow<T>.collectOnIO(): State<T?> {
    return collectOnIO(null)
}


//@Composable
//fun <T> Flow<T>.rememberCollectIO(): MutableState<T?> {
//    val latest = remember { mutableStateOf<T?>(null) }
//
//    LaunchedEffect(key1 = this) {
//        withContext(Dispatchers.IO) {
////            tjoi
//
//            collect { latest.value = it }
//
//        }
//    }
//
//    return latest
//}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun <T> StateFlow<T>.collectOnIO(): State<T> {
    return collectOnIO(value)
}

typealias Transform<T, R, S> = suspend (T, ProduceSideEffect<S>) -> R
typealias ProduceSideEffect<S> = (S) -> Unit

typealias ListSideEffectHandler<S> = SingleSideEffectHandler<List<S>>
typealias SingleSideEffectHandler<S> = suspend (S) -> Unit

inline fun <T, R, S> Flow<T>.mapProducingSideEffect(
    crossinline transform: Transform<T, R, S>,
    crossinline handleSideEffect: SingleSideEffectHandler<S>,
): Flow<R> {
    var sideEffect: S? = null

    val transformed = transform { value ->
        val result = transform(value) { sideEffect = it }
        return@transform emit(result)
    }

    return transformed.onCompletion {
        if (sideEffect != null) handleSideEffect(sideEffect!!)
    }
}


inline fun <T, R, S> Flow<T>.mapProducingSideEffects(
    crossinline transform: Transform<T, R, S>,
    crossinline handleSideEffects: ListSideEffectHandler<S>,
): Flow<R> {
    val sideEffects = mutableListOf<S>()

    val transformed = transform { value ->
        val result = transform(value, sideEffects::add)
        return@transform emit(result)
    }

    return transformed.onCompletion { handleSideEffects(sideEffects) }
}
