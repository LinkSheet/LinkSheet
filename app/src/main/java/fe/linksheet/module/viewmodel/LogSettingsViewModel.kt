package fe.linksheet.module.viewmodel

import android.app.Application
import fe.android.preference.helper.PreferenceRepository
import fe.linksheet.module.log.AppLogger
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.flowOfLazy

class LogSettingsViewModel(
    context: Application,
    val preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {

    private val appLogger = AppLogger.getInstance()

    val files = flowOfLazy {
        appLogger.getLogFiles()
    }
}