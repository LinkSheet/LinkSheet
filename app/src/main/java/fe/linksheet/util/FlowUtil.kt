package fe.linksheet.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


inline fun <T> flowOfLazy(crossinline value: () -> T): Flow<T> = flow {
    emit(value())
}
