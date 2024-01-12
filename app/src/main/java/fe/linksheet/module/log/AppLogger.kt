package fe.linksheet.module.log

import android.content.Context
import fe.gson.extension.io.fromJsonOrNull
import fe.gson.extension.io.toJson
import fe.kotlin.extension.primitive.unixMillisUtc
import fe.kotlin.extension.time.localizedString
import fe.kotlin.extension.time.unixMillis
import fe.linksheet.LinkSheetApp
import fe.linksheet.module.log.entry.LogEntry
import fe.linksheet.util.SingletonHolder
import java.io.File
import java.time.LocalDateTime


class AppLogger private constructor(app: LinkSheetApp) {
    companion object : SingletonHolder<AppLogger, LinkSheetApp>(::AppLogger) {
        const val LOG_DIR = "logs"
        const val FILE_EXT = ".log"
        const val FILE_EXT_V2 = ".json"
    }

    data class LogFile(val file: File, val millis: Long) {
        val localizedTime by lazy {
            millis.unixMillisUtc.value.localizedString()
        }

        companion object {
            fun new(logDir: File): LogFile {
                val millis = System.currentTimeMillis()
                return LogFile(File(logDir, "$millis$FILE_EXT_V2"), millis)
            }
        }
    }

    val startupTime: LocalDateTime = LocalDateTime.now()
    val logEntries = mutableListOf<LogEntry>()

    private val logDir = app.getDir(LOG_DIR, Context.MODE_PRIVATE)

    fun getLogFiles(): List<LogFile> {
        return logDir.listFiles()
            ?.filter { it.length() > 0L }
            ?.sortedDescending()
            ?.mapNotNull {
                val millis = it.nameWithoutExtension.substringBefore(".").toLongOrNull() ?: return@mapNotNull null
                LogFile(it, millis)
            } ?: emptyList()
    }

    fun deleteLogFile(logFile: LogFile): Boolean {
        return logFile.file.delete()
    }

    fun deleteOldLogs() {
        val startupMillis = startupTime.minusWeeks(2).unixMillis.millis
        getLogFiles().filter { it.millis < startupMillis }.forEach { deleteLogFile(it) }
    }

    fun readLogFile(name: String): List<LogEntry> {
        val logFile = File(logDir, name)

        if (logFile.extension == FILE_EXT) {
            return logFile
                .readLines().filter { it.isNotEmpty() }
                .mapNotNull { LogEntry.fromLogFileLine(it) }
        }

        return logFile.fromJsonOrNull<List<LogEntry>>() ?: emptyList()
    }

    fun write(entry: LogEntry) {
        logEntries.add(entry)
    }

    fun writeLog() {
        if (logEntries.isNotEmpty()) {
            LogFile.new(logDir).file.toJson(logEntries)
        }
    }
}
