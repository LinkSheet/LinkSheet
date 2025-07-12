package fe.linksheet.log

import android.util.Log

object LLog {
    var logLevel: Level = Level.Debug
    private val sinks = mutableListOf<LogSink>()

    fun addSink(sink: LogSink) {
        synchronized(sinks) {
            sinks.add(sink)
        }
    }

    fun log(
        priority: Level = Level.Debug,
        tag: String? = null,
        throwable: Throwable? = null,
        message: String,
    ) {
        if (priority.value < logLevel.value) return
        synchronized(sinks) {
            for (sink in sinks) {
                sink.log(priority, tag, throwable, message)
            }
        }
    }

    enum class Level(val value: Int) {
        Debug(Log.DEBUG),
        Info(Log.INFO),
        Warn(Log.WARN),
        Error(Log.ERROR)
    }
}
