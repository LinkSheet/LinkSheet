package fe.linksheet.module.viewmodel

import fe.linksheet.extension.android.launchIO
import fe.linksheet.module.log.file.FileAppLogger
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LogSettingsViewModel(
    val preferenceRepository: AppPreferenceRepository,
    val fileAppLogger: FileAppLogger
) : BaseViewModel(preferenceRepository) {

    val files = MutableStateFlow(emptyList<FileAppLogger.LogFile>())

    init {
        launchIO { files.emit(fileAppLogger.getLogFiles()) }
    }

    fun deleteFileAsync(logFile: FileAppLogger.LogFile) = launchIO {
        if (fileAppLogger.deleteLogFile(logFile)) {
            files.emit(fileAppLogger.getLogFiles())
        }
    }
}
