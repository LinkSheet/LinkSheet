package fe.linksheet.module.viewmodel

import android.app.Application
import com.google.gson.Gson
import fe.linksheet.feature.systeminfo.SystemInfoService
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class AboutSettingsViewModel(
    val context: Application,
    val gson: Gson,
    val infoService: SystemInfoService,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val devModeEnabled = preferenceRepository.asViewModelState(AppPreferences.devModeEnabled)

    fun getBuildInfo(): String {
        return gson.toJson(infoService.buildInfo)
    }
}
