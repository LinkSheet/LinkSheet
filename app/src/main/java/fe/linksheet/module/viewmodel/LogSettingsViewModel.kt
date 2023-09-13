package fe.linksheet.module.viewmodel

import android.util.Log
import fe.linksheet.module.preference.AppPreferenceRepository

import fe.kotlin.extension.localizedString
import fe.kotlin.extension.unixMillis
import fe.linksheet.extension.android.ioLaunch
import fe.linksheet.module.log.AppLogger
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LogSettingsViewModel(
    val preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {

    private val appLogger = AppLogger.getInstance()
    val files = MutableStateFlow(emptyMap<String, String>())

    private fun getLogFiles() = appLogger.getLogFiles().associateWith {
        it.toLong().unixMillis.value.localizedString()
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