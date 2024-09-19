package fe.linksheet.module.viewmodel

import fe.linksheet.extension.android.launchIO
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.log.file.LogSession
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LogSettingsViewModel(
    val preferenceRepository: AppPreferenceRepository,
    val logPersistService: LogPersistService
) : BaseViewModel(preferenceRepository) {

    val files = MutableStateFlow(emptyList<LogSession>())

    init {
        launchIO { files.emit(logPersistService.getLogSessions()) }
    }

    fun deleteFileAsync(session: LogSession) = launchIO {
        if (logPersistService.delete(session)) {
            files.emit(logPersistService.getLogSessions())
        }
    }
}
