package fe.linksheet.module.lifecycle

import androidx.lifecycle.Lifecycle

interface Service {
    fun boot(lifecycle: Lifecycle)

    fun shutdown(lifecycle: Lifecycle)
}
