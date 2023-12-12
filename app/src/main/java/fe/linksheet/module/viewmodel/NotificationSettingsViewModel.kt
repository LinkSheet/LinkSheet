package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.AppPreferenceRepository

import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class NotificationSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val urlCopiedToast = preferenceRepository.getBooleanState(AppPreferences.urlCopiedToast)
    val downloadStartedToast = preferenceRepository.getBooleanState(AppPreferences.downloadStartedToast)
    val openingWithAppToast = preferenceRepository.getBooleanState(AppPreferences.openingWithAppToast)
    val resolveViaToast = preferenceRepository.getBooleanState(AppPreferences.resolveViaToast)
    val resolveViaFailedToast = preferenceRepository.getBooleanState(AppPreferences.resolveViaFailedToast)

}