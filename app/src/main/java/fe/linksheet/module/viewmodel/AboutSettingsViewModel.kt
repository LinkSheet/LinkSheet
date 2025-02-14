package fe.linksheet.module.viewmodel

import android.app.Application
import com.google.gson.Gson
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.buildconfig.LinkSheetInfo

class AboutSettingsViewModel(
    val context: Application,
    val gson: Gson,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val devModeEnabled = preferenceRepository.asState(AppPreferences.devModeEnabled)

    fun getBuildInfo(): String {
        return gson.toJson(LinkSheetInfo.buildInfo)
    }
}
