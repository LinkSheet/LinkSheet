package fe.linksheet.module.log.file

import android.content.Context
import androidx.lifecycle.Lifecycle
import fe.gson.extension.io.fromJsonOrNull
import fe.gson.extension.io.toJson
import fe.kotlin.extension.primitive.unixMillisUtc
import fe.kotlin.extension.time.localizedString
import fe.kotlin.extension.time.unixMillis
import fe.linksheet.LinkSheetApp
import fe.linksheet.extension.koin.service
import fe.linksheet.module.lifecycle.Service
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.util.SingletonHolder
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.io.File
import java.time.LocalDateTime

val fileAppLoggerModule = module {
    service<FileAppLogger> {
        val logDir = FileAppLogger.getLogDir(applicationContext)
        FileAppLogger(logDir)
    }
}

class FileAppLogger(private val logDir: File) : Service {
    companion object {
        const val LOG_DIR = "logs"
        const val FILE_EXT = "log"
        const val FILE_EXT_V2 = "json"

        fun getLogDir(context: Context): File {
            return context.getDir(LOG_DIR, Context.MODE_PRIVATE)
        }
    }

    data class LogFile(val file: File, val millis: Long) {
        val localizedTime by lazy { millis.unixMillisUtc.value.localizedString() }

        companion object {
            fun new(logDir: File): LogFile {
                val millis = System.currentTimeMillis()
                return LogFile(File(logDir, "$millis.$FILE_EXT_V2"), millis)
            }
        }
    }

    val startupTime: LocalDateTime = LocalDateTime.now()
    val logEntries = mutableListOf<LogEntry>()

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

    override fun boot(lifecycle: Lifecycle) {
        val startupMillis = startupTime.minusWeeks(2).unixMillis.millis
        getLogFiles().filter { it.millis < startupMillis }.forEach { deleteLogFile(it) }
    }

    override fun shutdown(lifecycle: Lifecycle) {
        if (logEntries.isNotEmpty()) {
            LogFile.new(logDir).file.toJson(logEntries)
        }
    }
}
