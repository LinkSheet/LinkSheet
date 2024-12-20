package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.ClipboardManager
import androidx.core.content.getSystemService
import androidx.lifecycle.SavedStateHandle
import fe.linksheet.navigation.LogTextViewerRoute
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.viewmodel.base.SavedStateViewModel
import fe.linksheet.module.viewmodel.util.LogViewCommon
import kotlinx.coroutines.flow.map

class LogTextSettingsViewModel(
    context: Application,
    savedStateHandle: SavedStateHandle,
    val logViewCommon: LogViewCommon,
    val preferenceRepository: AppPreferenceRepository,
    private val logPersistService: LogPersistService,
) : SavedStateViewModel<LogTextViewerRoute>(savedStateHandle, preferenceRepository) {

    val clipboardManager = context.getSystemService<ClipboardManager>()!!
    private val sessionId = getSavedStateFlowNullable(LogTextViewerRoute::id)
    val sessionName = getSavedStateFlow(LogTextViewerRoute::name)

    val logEntries = sessionId.map { logPersistService.readEntries(it) }
}
