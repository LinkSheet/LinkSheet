package fe.linksheet.log

import android.os.SystemClock

class Logger(
    private val tag: String? = null,
) {
    fun debug(message: String, throwable: Throwable? = null) {
        Log.log(Log.Level.Debug, tag = tag, message = message, throwable = throwable)
    }

    fun info(message: String, throwable: Throwable? = null) {
        Log.log(Log.Level.Info, tag = tag, message = message, throwable = throwable)
    }

    fun warn(message: String, throwable: Throwable? = null) {
        Log.log(Log.Level.Warn, tag = tag, message = message, throwable = throwable)
    }

    fun error(message: String, throwable: Throwable? = null) {
        Log.log(Log.Level.Error, tag = tag, message = message, throwable = throwable)
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

        /**
         * Send a DEBUG log message.
         */
        fun debug(message: String, throwable: Throwable? = null) = DEFAULT.debug(message, throwable)

        /**
         * Send a INFO log message.
         */
        fun info(message: String, throwable: Throwable? = null) = DEFAULT.info(message, throwable)

        /**
         * Send a WARN log message.
         */
        fun warn(message: String, throwable: Throwable? = null) = DEFAULT.warn(message, throwable)

        /**
         * Send a ERROR log message.
         */
        fun error(message: String, throwable: Throwable? = null) = DEFAULT.error(message, throwable)

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
            return DEFAULT.measure(message, block)
        }
    }
}
