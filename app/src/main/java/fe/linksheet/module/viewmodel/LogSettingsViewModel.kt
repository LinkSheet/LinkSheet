package fe.linksheet.module.viewmodel

import androidx.lifecycle.ViewModel
import fe.linksheet.extension.android.launchIO
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.log.file.LogSession
import kotlinx.coroutines.flow.MutableStateFlow

class LogSettingsViewModel(
    val logPersistService: LogPersistService
) : ViewModel() {

    val files = MutableStateFlow(emptyList<LogSession>())

    init {
        launchIO { files.emit(logPersistService.getLogSessions()) }
    }

    fun deleteFileAsync(sessionId: String) = launchIO {
        if (logPersistService.delete(sessionId)) {
            files.emit(logPersistService.getLogSessions())
        }
    }
}
