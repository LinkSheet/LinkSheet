package fe.linksheet.extension.kotlinx

import kotlinx.coroutines.Deferred

suspend fun <T> Deferred<T>.awaitOrNull(): T? {
    return runCatching { await() }.getOrNull()
}
