package fe.linksheet.module.viewmodel


import android.app.Application
import androidx.lifecycle.viewModelScope
import app.linksheet.api.preference.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class GeneralSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val homeClipboardCard = preferenceRepository.asViewModelState(AppPreferences.homeClipboardCard)
}
