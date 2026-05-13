package fe.linksheet.module.viewmodel

import android.app.Application
import app.linksheet.api.preference.AppPreferenceRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel

class PretendToBeAppSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    companion object {
    }
}
