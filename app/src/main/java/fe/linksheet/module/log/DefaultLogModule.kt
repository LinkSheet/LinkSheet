package fe.linksheet.module.log

import fe.android.lifecycle.koin.extension.service
import fe.droidkit.koin.factory
import fe.droidkit.koin.single
import fe.kotlin.extension.string.decodeHexOrThrow
import fe.linksheet.LinkSheetApp
import fe.linksheet.module.log.file.LogFileService
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.log.internal.DefaultLoggerDelegate
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.redactor.DefaultRedactor
import fe.linksheet.module.redactor.Redactor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module
import kotlin.reflect.KClass

val DefaultLogModule = module {
    single<Redactor, AppPreferenceRepository> { _, preferences ->
        // TODO: Not optimal
        val logKey = runBlocking(Dispatchers.IO) {
            @OptIn(SensitivePreference::class)
            preferences.getOrPutInit(AppPreferences.logKey).decodeHexOrThrow()
        }

        DefaultRedactor(logKey)
    }
    service<LogPersistService> {
        val logDir = LogFileService.getLogDir(applicationContext)
        LogFileService(logDir, scope.get<LinkSheetApp>().startupTime)
    }
    factory<Logger, Redactor, LogPersistService> { params, redactor, logFileService ->
        val delegate = DefaultLoggerDelegate(params.get<KClass<*>>(), redactor, logFileService)
        Logger(delegate)
    }
}
