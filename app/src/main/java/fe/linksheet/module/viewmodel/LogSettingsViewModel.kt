package fe.linksheet.module.viewmodel

import fe.kotlin.extension.primitive.unixMillisUtc
import fe.kotlin.extension.time.localizedString
import fe.linksheet.extension.android.launchIO
import fe.linksheet.module.log.AppLogger
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LogSettingsViewModel(
    val preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {

    private val appLogger = AppLogger.getInstance()
    val files = MutableStateFlow(emptyMap<String, String>())

    private fun getLogFiles() = appLogger.getLogFiles().associateWith {
        it.toLong().unixMillisUtc.value.localizedString()
    }

    init {
        launchIO { files.emit(getLogFiles()) }
    }

    fun deleteFileAsync(name: String) = launchIO {
        if (appLogger.deleteLogFile(name)) {
            files.emit(getLogFiles())
        }
    }
}
