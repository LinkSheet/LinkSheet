package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.ClipboardManager
import androidx.core.content.getSystemService
import androidx.lifecycle.SavedStateHandle
import fe.android.preference.helper.PreferenceRepository
import fe.linksheet.LogTextViewerRoute
import fe.linksheet.module.log.AppLogger
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.viewmodel.base.SavedStateViewModel
import kotlinx.coroutines.flow.map

class LogTextSettingsViewModel(
    context: Application,
    savedStateHandle: SavedStateHandle,
    private val loggerFactory: LoggerFactory,
    val preferenceRepository: PreferenceRepository
) : SavedStateViewModel<LogTextViewerRoute>(savedStateHandle, preferenceRepository) {
    private val appLogger = AppLogger.getInstance()
    val clipboardManager = context.getSystemService<ClipboardManager>()!!

    val timestamp = getSavedStateFlow(LogTextViewerRoute::timestamp)
    private val fileName = getSavedStateFlowNullable(LogTextViewerRoute::fileName)

    val logEntries = fileName.map { file ->
        if (file == null) {
            appLogger.logEntries
        } else {
            appLogger.readLogFile(file)
        }
    }

    fun logPreferences() = Preferences.log(preferenceRepository) + Preferences.logPackages(
        loggerFactory.logHasher,
        preferenceRepository
    )
}