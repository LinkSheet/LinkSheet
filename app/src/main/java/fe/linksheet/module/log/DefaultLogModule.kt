package fe.linksheet.module.log

import app.linksheet.api.preference.AppPreferenceRepository
import fe.android.lifecycle.koin.extension.service
import fe.composekit.mozilla.components.support.base.log.Log
import fe.composekit.mozilla.components.support.base.log.sink.LogSink
import fe.composekit.preference.asFlow
import fe.composekit.preference.asFunction
import fe.linksheet.LinkSheetApp
import fe.linksheet.module.log.file.LogFileService
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.preference.app.AppPreferences
import org.koin.dsl.module

val DefaultLogModule = module {
    service<LogPersistService, AppPreferenceRepository> { _, appPreferenceRepository ->
        val logDir = LogFileService.getLogDir(applicationContext)
        LogFileService(
            logDir = logDir,
            disableLogs = appPreferenceRepository.asFlow(AppPreferences.privacy.incognito.disableLogs),
            persistenceDisabled = appPreferenceRepository.asFunction(AppPreferences.privacy.incognito.disableLogsPersistence),
            startupTime = scope.get<LinkSheetApp>().startupTime
        )
    }
}

class FileLogSink(private val service: LogPersistService) : LogSink {
    override fun log(
        priority: Log.Priority,
        tag: String?,
        throwable: Throwable?,
        message: String
    ) {
        val message = "$message ${android.util.Log.getStackTraceString(throwable)}".trim()
        val entry = LogEntry.DefaultLogEntry(
            priority.name,
            prefix = tag ?: "<none>",
            message = message,
        )

        service.write(entry)
    }
}
