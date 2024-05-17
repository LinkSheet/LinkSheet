package fe.linksheet.extension.kotlinx

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend fun <T> Deferred<T>.awaitOrNull(): T? {
    return runCatching { await() }.onFailure { currentCoroutineContext().ensureActive() }.getOrNull()
}
