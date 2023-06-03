package fe.linksheet.module.log

import android.content.Context
import fe.linksheet.LinkSheetApp
import fe.linksheet.extension.decodeToString
import fe.linksheet.extension.encodeFromString
import fe.linksheet.util.SingletonHolder
import fe.stringbuilder.util.buildSeparatedString
import java.io.File
import java.time.LocalDateTime
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
data class LogEntry(
    val type: String,
    val unixMillis: Long,
    val prefix: String,
    val message: String,
    val redactedMessage: String,
) {
    val messagePrefix = "$unixMillis $type $prefix: "

    override fun toString() = buildSeparatedString(" ") {
        item { append(type) }
        item { append(unixMillis) }
        item { append(prefix) }
        item { append(Base64.encodeFromString(message)) }
        item { append(Base64.encodeFromString(redactedMessage)) }
    }

    companion object {
        fun fromLogFileLine(line: String): LogEntry {
            val (type, unixMillis, prefix, message, redactedMessage) = line.split(" ")

            return LogEntry(
                type,
                unixMillis.toLong(),
                prefix,
                Base64.decodeToString(message),
                Base64.decodeToString(redactedMessage)
            )
        }
    }
}

class AppLogger private constructor(private val app: LinkSheetApp) {
    companion object : SingletonHolder<AppLogger, LinkSheetApp>(::AppLogger) {
        const val logDir = "logs"
        const val fileExt = ".log"
    }

    val startupTime = LocalDateTime.now()
    val logEntries = mutableListOf<LogEntry>()

    fun getLogFiles() = app.getDir(logDir, Context.MODE_PRIVATE).listFiles()
        ?.filter { it.length() > 0L }
        ?.sortedDescending()?.map { file ->
            file.name.substring(0, file.name.indexOf(fileExt))
        } ?: emptyList()

    fun readLogFile(name: String) = File(app.getDir(logDir, Context.MODE_PRIVATE), name + fileExt)
        .readLines()
        .filter { it.isNotEmpty() }
        .map { line -> LogEntry.fromLogFileLine(line) }

    private fun newWriter() = File(
        app.getDir(logDir, Context.MODE_PRIVATE),
        "${System.currentTimeMillis()}$fileExt"
    ).bufferedWriter()

    fun write(entry: LogEntry) {
        logEntries.add(entry)
    }

    fun writeLog() {
        if (logEntries.isNotEmpty()) {
            newWriter().use { logWriter ->
                logEntries.forEach {
                    logWriter.write(it.toString())
                    logWriter.newLine()
                }
            }
        }
    }
}