package fe.linksheet.extension.kotlin

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


typealias Transform<T, R, S> = suspend (item: T, produce: ProduceSideEffect<S>) -> R
typealias ProduceSideEffect<S> = (sideEffect: S) -> Unit

typealias ListSideEffectHandler<S> = SingleSideEffectHandler<List<S>>
typealias SingleSideEffectHandler<S> = suspend (sideEffect: S) -> Unit

inline fun <T, R, S> Flow<T>.mapProducingSideEffect(
    sideEffectContext: CoroutineContext,
    crossinline transform: Transform<T, R, S>,
    crossinline handleSideEffect: SingleSideEffectHandler<S>,
): Flow<R> {
    var sideEffect: S? = null

    val transformed = transform { value ->
        val result = transform(value) { sideEffect = it }
        return@transform emit(result)
    }

    return transformed.onCompletion {
        if (sideEffect != null) {
            withContext(sideEffectContext) { handleSideEffect(sideEffect!!) }
        }
    }
}


inline fun <T, R, S> Flow<T>.mapProducingSideEffects(
    sideEffectContext: CoroutineContext,
    crossinline transform: Transform<T, R, S>,
    crossinline handleSideEffects: ListSideEffectHandler<S>,
): Flow<R> {
    val sideEffects = mutableListOf<S>()

    val transformed = transform { value ->
        val result = transform(value, sideEffects::add)
        return@transform emit(result)
    }

    return transformed.onCompletion {
        withContext(sideEffectContext) { handleSideEffects(sideEffects) }
    }
}
