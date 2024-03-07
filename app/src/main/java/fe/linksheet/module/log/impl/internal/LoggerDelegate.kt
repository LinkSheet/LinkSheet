package fe.linksheet.module.log.impl.internal

import android.util.Log
import fe.linksheet.module.log.file.LogFileService
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.redactor.Redactor

typealias ProduceMessage = (String) -> String

abstract class LoggerDelegate(
    private val prefix: String,
    val redactor: Redactor,
    private val fileAppLogger: LogFileService
) {
    enum class Level(val code: String) {
        Verbose("V"), Info("I"), Debug("D"), Error("E")
    }

    private fun printLogcat(level: Level, data: Logcat, subPrefix: String?) {
        val (msg, tr) = data
        val mergedMsg = (subPrefix?.let { "[$it] " } ?: "") + msg
        when (level) {
            Level.Verbose -> if (tr != null) Log.v(prefix, mergedMsg, tr) else Log.v(prefix, mergedMsg)
            Level.Info -> if (tr != null) Log.i(prefix, mergedMsg, tr) else Log.i(prefix, mergedMsg)
            Level.Debug -> if (tr != null) Log.d(prefix, mergedMsg, tr) else Log.d(prefix, mergedMsg)
            Level.Error -> if (tr != null) Log.e(prefix, mergedMsg, tr) else Log.e(prefix, mergedMsg)
        }
    }

    private fun writeFileLogEntry(level: Level, fileLogEntry: FileLogEntry, subPrefix: String?) {
        val (plain, redacted) = fileLogEntry
        fileAppLogger.write(
            LogEntry.DefaultLogEntry(
                level.code,
                prefix = prefix + (subPrefix?.let { "/$it" } ?: ""),
                message = plain,
                redactedMessage = redacted
            )
        )
    }

    protected abstract fun <T> redactParameter(
        msg: ProduceMessage,
        param: T,
        processor: HashProcessor<T>
    ): Pair<String, String>

    private fun print(level: Level, logcat: Logcat, fileLogEntry: FileLogEntry, subPrefix: String? = null) {
        printLogcat(level, logcat, subPrefix)
        writeFileLogEntry(level, fileLogEntry, subPrefix)
    }

    data class Logcat(val msg: String? = null, val throwable: Throwable? = null)
    data class FileLogEntry(val plain: String, val redacted: String? = null)

    fun <T> log(level: Level, param: T, processor: HashProcessor<T>, msg: ProduceMessage, subPrefix: String? = null) {
        val (plainMsg, redactedMsg) = redactParameter(msg, param, processor)
        print(level, Logcat(plainMsg), FileLogEntry(plainMsg, redactedMsg), subPrefix)
    }

    fun log(level: Level, msg: String? = null, throwable: Throwable? = null, subPrefix: String? = null) {
        print(
            level,
            Logcat(msg, throwable),
            FileLogEntry("$msg ${Log.getStackTraceString(throwable)}".trim()),
            subPrefix
        )
    }

    fun fatal(stacktrace: String) {
        Log.wtf(prefix, stacktrace)
        fileAppLogger.write(LogEntry.FatalEntry(message = stacktrace))
    }
}


