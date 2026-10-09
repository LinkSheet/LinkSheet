package fe.linksheet.module.viewmodel

import android.app.Application
import app.linksheet.api.SystemInfoService
import com.google.gson.Gson
import app.linksheet.api.preference.AppPreferenceRepository
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
