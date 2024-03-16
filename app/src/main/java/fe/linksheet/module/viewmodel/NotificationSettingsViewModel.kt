package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.app.AppPreferenceRepository


import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class NotificationSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val urlCopiedToast = preferenceRepository.asState(AppPreferences.urlCopiedToast)
    val downloadStartedToast = preferenceRepository.asState(AppPreferences.downloadStartedToast)
    val openingWithAppToast = preferenceRepository.asState(AppPreferences.openingWithAppToast)
    val resolveViaToast = preferenceRepository.asState(AppPreferences.resolveViaToast)
    val resolveViaFailedToast = preferenceRepository.asState(AppPreferences.resolveViaFailedToast)

}
