package fe.linksheet.util

import androidx.compose.runtime.MutableState
import fe.linksheet.extension.setup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> io(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO) {
    block(this)
}

suspend fun <T> io(loading: (Boolean) -> Unit, block: suspend CoroutineScope.() -> T) = io {
    loading(true)
    val result = block()
    loading(false)

    result
}

suspend inline fun <T> io(
    loading: MutableState<Boolean>,
    crossinline block: suspend CoroutineScope.() -> T
) = io {
    loading.value = true
    val result = block()
    loading.value = false

    result
}

suspend fun <T> io(
    list: MutableList<T>,
    block: suspend CoroutineScope.() -> Iterable<T>
) = io {
    list.setup(block())
}

suspend fun <T> io(
    loading: MutableState<Boolean>,
    list: MutableList<T>,
    block: suspend CoroutineScope.() -> Iterable<T>
) = io {
    loading.value = true
    list.setup(block())
    loading.value = false
}