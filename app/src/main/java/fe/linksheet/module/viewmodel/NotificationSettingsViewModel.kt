package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.app.AppPreferenceRepository


import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class NotificationSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val urlCopiedToast = preferenceRepository.asViewModelState(AppPreferences.notifications.urlCopiedToast)
    val downloadStartedToast = preferenceRepository.asViewModelState(AppPreferences.notifications.downloadStartedToast)
    val openingWithAppToast = preferenceRepository.asViewModelState(AppPreferences.notifications.openingWithAppToast)
    val resolveViaToast = preferenceRepository.asViewModelState(AppPreferences.notifications.resolveViaToast)
    val resolveViaFailedToast = preferenceRepository.asViewModelState(AppPreferences.notifications.resolveViaFailedToast)
}
