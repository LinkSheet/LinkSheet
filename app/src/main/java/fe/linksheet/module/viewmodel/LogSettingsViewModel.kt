package fe.linksheet.module.viewmodel

import fe.linksheet.extension.android.launchIO
import fe.linksheet.module.log.AppLogger
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LogSettingsViewModel(
    val preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {

    private val appLogger = AppLogger.getInstance()
    val files = MutableStateFlow(emptyList<AppLogger.LogFile>())

    private fun getLogFiles() = appLogger.getLogFiles()

    init {
        launchIO { files.emit(getLogFiles()) }
    }

    fun deleteFileAsync(logFile: AppLogger.LogFile) = launchIO {
        if (appLogger.deleteLogFile(logFile)) {
            files.emit(getLogFiles())
        }
    }
}
