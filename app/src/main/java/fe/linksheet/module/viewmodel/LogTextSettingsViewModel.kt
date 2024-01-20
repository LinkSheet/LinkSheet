package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.ClipboardManager
import androidx.core.content.getSystemService
import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import fe.linksheet.module.preference.AppPreferenceRepository

import fe.linksheet.LogTextViewerRoute
import fe.linksheet.module.log.AppLogger
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.viewmodel.base.SavedStateViewModel
import fe.linksheet.module.viewmodel.util.LogViewCommon
import kotlinx.coroutines.flow.map

class LogTextSettingsViewModel(
    context: Application,
    savedStateHandle: SavedStateHandle,
    loggerFactory: LoggerFactory,
    gson: Gson,
    val preferenceRepository: AppPreferenceRepository
) : SavedStateViewModel<LogTextViewerRoute>(savedStateHandle, preferenceRepository) {
    private val appLogger = AppLogger.getInstance()
    val clipboardManager = context.getSystemService<ClipboardManager>()!!
    val logViewCommon = LogViewCommon(
        preferenceRepository,
        gson,
        loggerFactory.createLogger(LogTextSettingsViewModel::class)
    )

    val timestamp = getSavedStateFlow(LogTextViewerRoute::timestamp)
    private val fileName = getSavedStateFlowNullable(LogTextViewerRoute::fileName)

    val logEntries = fileName.map { file ->
        if (file == null) {
            appLogger.logEntries
        } else {
            appLogger.readLogFile(file)
        }
    }
}
