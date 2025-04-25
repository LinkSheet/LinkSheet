package fe.linksheet.module.log.file

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import fe.android.lifecycle.LifecycleAwareService
import fe.gson.extension.io.fromJsonOrNull
import fe.gson.extension.io.toJson
import fe.linksheet.module.log.file.entry.LogEntry
import fe.std.javatime.extension.unixMillisUtc
import fe.std.javatime.time.localizedString
import fe.std.javatime.time.unixMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

@Suppress("FunctionName")
fun DebugLogPersistService(startupTime: LocalDateTime = LocalDateTime.now()): LogPersistService {
    return object : LogPersistService {
        override val startupTime: LocalDateTime = startupTime
        override fun readEntries(sessionId: String?): List<LogEntry> = emptyList()
        override fun getLogSessions(): List<LogSession> = emptyList()
        override fun delete(sessionId: String): Boolean = true
        override fun write(entry: LogEntry) {}
    }
}

interface LogPersistService : LifecycleAwareService {
    val startupTime: LocalDateTime
    fun readEntries(sessionId: String?): List<LogEntry>
    fun getLogSessions(): List<LogSession>
    fun delete(sessionId: String): Boolean
    fun write(entry: LogEntry)
}

abstract class LogSession(
    val id: String,
    val info: String,
)

internal class LogFileService(logDir: Lazy<File>, override val startupTime: LocalDateTime) : LogPersistService {
    private val logDir by logDir

    companion object {
        private const val LOG_DIR = "logs"
        const val FILE_EXT = "log"
        const val FILE_EXT_V2 = "json"

        fun getLogDir(context: Context): Lazy<File> {
            return lazy { context.getDir(LOG_DIR, Context.MODE_PRIVATE) }
        }

        private fun createLogFile(file: File): LogFile? {
            return file.nameWithoutExtension
                .substringBefore(".")
                .toLongOrNull()
                ?.let { LogFile(file, it) }
        }
    }

    data class LogFile(
        val file: File,
        val millis: Long,
    ) : LogSession(file.name, millis.unixMillisUtc.value.localizedString()) {

        companion object {
            fun new(logDir: File): LogFile {
                val millis = System.currentTimeMillis()
                val file = File(logDir, "$millis.$FILE_EXT_V2")
                return LogFile(file, millis)
            }
        }
    }

    val logEntries = mutableListOf<LogEntry>()

    fun readLogFile(name: String): List<LogEntry> {
        val logFile = File(logDir, name)

        if (logFile.extension == FILE_EXT) {
            return logFile.readLines()
                .filter { it.isNotEmpty() }
                .mapNotNull { LogEntry.fromLogFileLine(it) }
        }

        return logFile.fromJsonOrNull<List<LogEntry?>>()
            ?.filterNotNull()
            ?: emptyList()
    }

    override fun write(entry: LogEntry) {
        logEntries.add(entry)
    }

    override fun readEntries(sessionId: String?): List<LogEntry> {
        return if (sessionId == null) logEntries else readLogFile(sessionId)
    }

    override fun getLogSessions(): List<LogFile> {
        return logDir.listFiles()
            ?.filter { it.length() > 0L }
            ?.sortedDescending()
            ?.mapNotNull(::createLogFile)
            ?: emptyList()
    }

    override fun delete(sessionId: String): Boolean {
        val file = File(logDir, sessionId)
        return if (file.exists()) file.delete() else false
    }

    private fun delete(session: LogFile): Boolean {
        return session.file.delete()
    }

    override suspend fun onAppInitialized(owner: LifecycleOwner) = withContext(Dispatchers.IO) {
        val startupMillis = startupTime.minusWeeks(2).unixMillis.millis
        getLogSessions().filter { it.millis < startupMillis }.forEach { delete(it) }
    }

    override suspend fun onStop() = withContext(Dispatchers.IO) {
        val immutable = logEntries.toList()
        if (immutable.isNotEmpty()) {
            LogFile.new(logDir).file.toJson(immutable)
        }
    }
}
