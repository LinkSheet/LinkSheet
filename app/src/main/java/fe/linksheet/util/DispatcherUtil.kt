package fe.linksheet.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> contextIO(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO) {
    block(this)
}

suspend fun <T> contextIO(loading: (Boolean) -> Unit, block: suspend CoroutineScope.() -> T) = contextIO {
    loading(true)
    val result = block()
    loading(false)

    result
}