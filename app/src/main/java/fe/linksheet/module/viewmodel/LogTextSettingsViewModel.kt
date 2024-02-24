package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.ClipboardManager
import androidx.core.content.getSystemService
import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import fe.linksheet.LogTextViewerRoute
import fe.linksheet.module.log.factory.LoggerFactory
import fe.linksheet.module.log.file.FileAppLogger
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.viewmodel.base.SavedStateViewModel
import fe.linksheet.module.viewmodel.util.LogViewCommon
import kotlinx.coroutines.flow.map

class LogTextSettingsViewModel(
    context: Application,
    savedStateHandle: SavedStateHandle,
    loggerFactory: LoggerFactory,
    gson: Gson,
    val preferenceRepository: AppPreferenceRepository,
    val fileAppLogger: FileAppLogger
) : SavedStateViewModel<LogTextViewerRoute>(savedStateHandle, preferenceRepository) {
    val clipboardManager = context.getSystemService<ClipboardManager>()!!
    val logViewCommon = LogViewCommon(
        preferenceRepository,
        gson,
        loggerFactory.createLogger(LogTextSettingsViewModel::class)
    )

    val timestamp = getSavedStateFlow(LogTextViewerRoute::timestamp)
    private val fileName = getSavedStateFlowNullable(LogTextViewerRoute::fileName)

    val logEntries = fileName.map { file ->
        if (file == null) fileAppLogger.logEntries
        else fileAppLogger.readLogFile(file)
    }
}
