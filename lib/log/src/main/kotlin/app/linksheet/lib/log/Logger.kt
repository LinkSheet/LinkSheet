package app.linksheet.lib.log

import android.os.SystemClock

class Logger(
    private val tag: String? = null,
) {
    fun debug(message: String, throwable: Throwable? = null) {
        LLog.log(LLog.Level.Debug, tag = tag, message = message, throwable = throwable)
    }

    fun info(message: String, throwable: Throwable? = null) {
        LLog.log(LLog.Level.Info, tag = tag, message = message, throwable = throwable)
    }

    fun warn(message: String, throwable: Throwable? = null) {
        LLog.log(LLog.Level.Warn, tag = tag, message = message, throwable = throwable)
    }

    fun error(message: String, throwable: Throwable? = null) {
        LLog.log(LLog.Level.Error, tag = tag, message = message, throwable = throwable)
    }

    /**
     * Measure the time it takes to execute the provided block and print a log message before and
     * after executing the block.
     *
     * Example log message:
     *   ⇢ doSomething()
     *   [..]
     *   ⇠ doSomething() [12ms]
     */
    fun measure(message: String, block: () -> Unit) {
        debug("⇢ $message")

        val start = SystemClock.elapsedRealtime()

        try {
            block()
        } finally {
            val took = SystemClock.elapsedRealtime() - start
            debug("⇠ $message [${took}ms]")
        }
    }

    companion object {
        private val DEFAULT = Logger()

        fun debug(message: String, throwable: Throwable? = null) = DEFAULT.debug(message, throwable)

        fun info(message: String, throwable: Throwable? = null) = DEFAULT.info(message, throwable)

        fun warn(message: String, throwable: Throwable? = null) = DEFAULT.warn(message, throwable)

        fun error(message: String, throwable: Throwable? = null) = DEFAULT.error(message, throwable)

        fun measure(message: String, block: () -> Unit) {
            return DEFAULT.measure(message, block)
        }
    }
}
