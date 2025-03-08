package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class FollowRedirectsSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val followRedirects = preferenceRepository.asViewModelState(AppPreferences.followRedirects)
    val followRedirectsLocalCache = preferenceRepository.asViewModelState(AppPreferences.followRedirectsLocalCache)
    val followRedirectsExternalService = preferenceRepository.asViewModelState(AppPreferences.followRedirectsExternalService)
    val followOnlyKnownTrackers = preferenceRepository.asViewModelState(AppPreferences.followOnlyKnownTrackers)
    val followRedirectsAllowsDarknets = preferenceRepository.asViewModelState(AppPreferences.followRedirectsAllowDarknets)
    val followRedirectsSkipBrowser = preferenceRepository.asViewModelState(AppPreferences.followRedirectsSkipBrowser)
    val requestTimeout = preferenceRepository.asViewModelState(AppPreferences.requestTimeout)
}
