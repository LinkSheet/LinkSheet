package fe.linksheet.module.viewmodel

import android.util.Log
import fe.android.preference.helper.PreferenceRepository
import fe.kotlin.extension.asUnixMillisToLocalDateTime
import fe.kotlin.extension.localizedString
import fe.linksheet.extension.android.ioLaunch
import fe.linksheet.module.log.AppLogger
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LogSettingsViewModel(
    val preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {

    private val appLogger = AppLogger.getInstance()
    val files = MutableStateFlow(emptyMap<String, String>())

    private fun getLogFiles() = appLogger.getLogFiles().associateWith {
        it.toLong().asUnixMillisToLocalDateTime().localizedString()!!
    }

    init {
        ioLaunch { files.emit(getLogFiles()) }
    }

    fun deleteFileAsync(name: String) = ioLaunch {
        if (appLogger.deleteLogFile(name).also { Log.d("DeleteFile", "$it") }) {
            files.emit(getLogFiles())
        }
    }
}