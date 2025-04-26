package fe.linksheet.experiment.engine

import android.util.Log

abstract class EngineLogger(protected val tag: String) {
    abstract fun debug(message: () -> String)
}

class AndroidEngineLogger(tag: String) : EngineLogger(tag) {
    override fun debug(message: () -> String) {
        Log.d(tag, message())
    }
}
