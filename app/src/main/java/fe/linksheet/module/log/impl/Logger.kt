package fe.linksheet.module.log.impl

import android.util.Log
import fe.linksheet.module.log.file.FileAppLogger
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.log.impl.hasher.HashProcessor
import fe.linksheet.module.log.impl.hasher.LogDumpable
import fe.linksheet.module.log.impl.hasher.LogHasher

abstract class Logger(val prefix: String, val hasher: LogHasher, val fileAppLogger: FileAppLogger) {
    enum class Type(val code: String) {
        Verbose("V"), Info("I"), Debug("D"), Error("E")
    }

    protected fun mergeSubPrefix(msg: String, subPrefix: String?) = (subPrefix?.let { "[$it] " } ?: "") + msg

    protected abstract fun print(type: Type, msg: String, subPrefix: String? = null)

    fun logFatal(stacktrace: String): String {
        Log.wtf("Crash", stacktrace)

        fileAppLogger.write(LogEntry.FatalEntry(System.currentTimeMillis(), stacktrace))
        return stacktrace
    }

    fun <T> dumpParameterToString(
        redact: Boolean = false,
        param: T,
        hashProcessor: HashProcessor<T>
    ): String {
        return dumpParameterToString(
            if (redact) hasher else LogHasher.NoOpHasher,
            param,
            hashProcessor
        )
    }

    private fun <T> dumpParameterToString(
        hasher: LogHasher,
        param: T,
        hashProcessor: HashProcessor<T>
    ): String {
        return LogDumpable.dumpObject(StringBuilder(), hasher, param, hashProcessor).toString()
            .replace("%", "%%")
    }

    abstract fun <T> verbose(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String? = null
    )

    abstract fun verbose(throwable: Throwable, subPrefix: String? = null)
    abstract fun verbose(msg: String, subPrefix: String? = null)
    abstract fun <T> info(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String? = null
    )

    abstract fun info(throwable: Throwable, subPrefix: String? = null)
    abstract fun info(msg: String, subPrefix: String? = null)

    abstract fun <T> debug(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String? = null
    )

    abstract fun debug(throwable: Throwable, subPrefix: String? = null)
    abstract fun debug(msg: String, subPrefix: String? = null)

    abstract fun <T> error(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String? = null
    )

    abstract fun error(throwable: Throwable, subPrefix: String? = null)
    abstract fun error(msg: String, subPrefix: String? = null)
}
