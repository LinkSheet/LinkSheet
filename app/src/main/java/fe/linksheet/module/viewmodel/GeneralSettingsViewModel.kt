package fe.linksheet.module.viewmodel


import android.app.Application
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class GeneralSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val homeClipboardCard = preferenceRepository.asState(AppPreferences.homeClipboardCard)
}
