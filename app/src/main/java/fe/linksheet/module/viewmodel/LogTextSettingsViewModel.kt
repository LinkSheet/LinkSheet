package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getState
import fe.linksheet.LibRedirectServiceRoute
import fe.linksheet.LinkSheetApp
import fe.linksheet.LogTextViewerRoute
import fe.linksheet.module.log.AppLogger
import fe.linksheet.module.log.LogEntry
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.module.viewmodel.base.SavedStateViewModel
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class LogTextSettingsViewModel(
    context: Application,
    savedStateHandle: SavedStateHandle,
    val preferenceRepository: PreferenceRepository
) : SavedStateViewModel<LogTextViewerRoute>(savedStateHandle, preferenceRepository) {
    private val appLogger = AppLogger.getInstance()

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