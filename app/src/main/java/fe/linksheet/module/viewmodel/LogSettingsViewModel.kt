package fe.linksheet.module.viewmodel

import fe.linksheet.extension.android.launchIO
import fe.linksheet.module.log.file.LogFileService
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LogSettingsViewModel(
    val preferenceRepository: AppPreferenceRepository,
    val fileAppLogger: LogFileService
) : BaseViewModel(preferenceRepository) {

    val files = MutableStateFlow(emptyList<LogFileService.LogFile>())

    init {
        launchIO { files.emit(fileAppLogger.getLogFiles()) }
    }

    fun deleteFileAsync(logFile: LogFileService.LogFile) = launchIO {
        if (fileAppLogger.deleteLogFile(logFile)) {
            files.emit(fileAppLogger.getLogFiles())
        }
    }
}
