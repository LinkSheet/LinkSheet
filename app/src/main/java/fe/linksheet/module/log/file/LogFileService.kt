package fe.linksheet.module.log.file

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import fe.android.lifecycle.LifecycleAwareService
import fe.gson.extension.io.fromJsonOrNull
import fe.gson.extension.io.toJson
import fe.kotlin.extension.primitive.unixMillisUtc
import fe.kotlin.extension.time.localizedString
import fe.kotlin.extension.time.unixMillis
import fe.linksheet.extension.koin.service
import fe.linksheet.module.log.file.entry.LogEntry
import org.koin.dsl.module
import java.io.File
import java.time.LocalDateTime

val logFileServiceModule = module {
    service<LogPersistService> {
        val logDir = LogFileService.getLogDir(applicationContext)
        LogFileService(logDir, applicationContext.startupTime)
    }
}

fun DebugLogPersistService(startupTime: LocalDateTime = LocalDateTime.now()): LogPersistService {
    return object : LogPersistService {
        override val startupTime: LocalDateTime = startupTime

        override fun readEntries(id: String?): List<LogEntry> = emptyList()

        override fun getLogSessions(): List<LogSession> = emptyList()

        override fun delete(session: LogSession): Boolean = true

        override fun write(entry: LogEntry) {}
    }
}


interface LogPersistService : LifecycleAwareService {
    val startupTime: LocalDateTime

    fun readEntries(id: String?): List<LogEntry>

    fun getLogSessions(): List<LogSession>
    fun delete(session: LogSession): Boolean
    fun write(entry: LogEntry)
}

abstract class LogSession(
    val id: String,
    val info: String,
)

private class LogFileService(private val logDir: File, override val startupTime: LocalDateTime) : LogPersistService {
    companion object {
        private const val LOG_DIR = "logs"
        const val FILE_EXT = "log"
        const val FILE_EXT_V2 = "json"

        fun getLogDir(context: Context): File {
            return context.getDir(LOG_DIR, Context.MODE_PRIVATE)
        }
    }

    data class LogFile(
        val file: File,
        val millis: Long,
    ) : LogSession(file.name, millis.unixMillisUtc.value.localizedString()) {

        companion object {
            fun new(logDir: File): LogFile {
                val millis = System.currentTimeMillis()
                return LogFile(File(logDir, "$millis.$FILE_EXT_V2"), millis)
            }
        }
    }

    val logEntries = mutableListOf<LogEntry>()


    fun readLogFile(name: String): List<LogEntry> {
        val logFile = File(logDir, name)

        if (logFile.extension == FILE_EXT) {
            return logFile
                .readLines().filter { it.isNotEmpty() }
                .mapNotNull { LogEntry.fromLogFileLine(it) }
        }

        return logFile.fromJsonOrNull<List<LogEntry?>>()?.filterNotNull() ?: emptyList()
    }

    override fun write(entry: LogEntry) {
        logEntries.add(entry)
    }

    override fun readEntries(id: String?): List<LogEntry> {
        return if (id == null) logEntries else readLogFile(id)
    }

    override fun getLogSessions(): List<LogFile> {
        return logDir.listFiles()
            ?.filter { it.length() > 0L }
            ?.sortedDescending()
            ?.mapNotNull {
                val millis = it.nameWithoutExtension.substringBefore(".").toLongOrNull() ?: return@mapNotNull null
                LogFile(it, millis)
            } ?: emptyList()
    }

    override fun delete(session: LogSession): Boolean {
        val file = File(logDir, session.id)
        return if (file.exists()) file.delete() else false
    }

    private fun delete(session: LogFile): Boolean {
        return session.file.delete()
    }

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        val startupMillis = startupTime.minusWeeks(2).unixMillis.millis
        getLogSessions().filter { it.millis < startupMillis }.forEach { delete(it) }
    }

    override suspend fun onStop() {
        val immutable = logEntries.toList()
        if (immutable.isNotEmpty()) {
            LogFile.new(logDir).file.toJson(immutable)
        }
    }
}
