package fe.linksheet.lifecycle

import androidx.lifecycle.Lifecycle

interface Service {
    fun onAppInitialized(lifecycle: Lifecycle) {}

    fun onPause(lifecycle: Lifecycle) {}

    fun onStop(lifecycle: Lifecycle)
}
