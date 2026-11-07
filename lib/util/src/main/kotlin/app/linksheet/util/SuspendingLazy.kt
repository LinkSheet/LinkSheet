package app.linksheet.util

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal object UNINITIALIZED_VALUE

// Via https://github.com/Kotlin/kotlinx.coroutines/issues/706#issuecomment-1041008405
class SuspendingLazy<T>(initializer: suspend () -> T) {
    private var initializer: (suspend () -> T)? = initializer
    private var mutex: Mutex? = Mutex()

    //    private var _value: T? = null
    private var _value: Any? = UNINITIALIZED_VALUE

    suspend fun value(): T {
        val _v1 = _value
        if (_v1 !== UNINITIALIZED_VALUE) {
            @Suppress("UNCHECKED_CAST")
            return _v1 as T
        }
        val m = mutex ?: return _v1 as T // Warning: unchecked cast.
        return m.withLock {
            val _v2 = _value
            if (_v2 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST") (_v2 as T)
            } else {
                val typedValue = initializer!!()
                _value = typedValue
                initializer = null
                mutex = null
                typedValue
            }
        }
    }
}
