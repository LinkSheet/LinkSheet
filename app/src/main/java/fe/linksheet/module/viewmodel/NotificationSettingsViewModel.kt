package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import fe.linksheet.module.preference.app.AppPreferenceRepository


import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class NotificationSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val urlCopiedToast = preferenceRepository.asViewModelState(AppPreferences.urlCopiedToast)
    val downloadStartedToast = preferenceRepository.asViewModelState(AppPreferences.downloadStartedToast)
    val openingWithAppToast = preferenceRepository.asViewModelState(AppPreferences.openingWithAppToast)
    val resolveViaToast = preferenceRepository.asViewModelState(AppPreferences.resolveViaToast)
    val resolveViaFailedToast = preferenceRepository.asViewModelState(AppPreferences.resolveViaFailedToast)
}
