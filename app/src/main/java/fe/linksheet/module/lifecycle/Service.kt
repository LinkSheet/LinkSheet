package fe.linksheet.module.lifecycle

import androidx.lifecycle.Lifecycle

interface Service {
    fun start(lifecycle: Lifecycle) {}

    fun stop(lifecycle: Lifecycle)
}
