package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences

class FollowRedirectsSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : ViewModel() {
    val followRedirects = preferenceRepository.asViewModelState(AppPreferences.followRedirects.enable)
    val followRedirectsMode = preferenceRepository.asViewModelState(AppPreferences.followRedirects.mode)
    val followRedirectsAggressive = preferenceRepository.asViewModelState(AppPreferences.followRedirects.aggressive)
    val followRedirectsLocalCache = preferenceRepository.asViewModelState(AppPreferences.followRedirects.localCache)
    val followRedirectsExternalService = preferenceRepository.asViewModelState(AppPreferences.followRedirects.externalService)
    val followOnlyKnownTrackers = preferenceRepository.asViewModelState(AppPreferences.followRedirects.onlyKnownTrackers)
    val followRedirectsAllowsDarknets = preferenceRepository.asViewModelState(AppPreferences.followRedirects.allowDarknets)
    val followRedirectsAllowLocalNetwork = preferenceRepository.asViewModelState(AppPreferences.followRedirects.allowLocalNetwork)
    val followRedirectsSkipBrowser = preferenceRepository.asViewModelState(AppPreferences.followRedirects.skipBrowser)
    val requestTimeout = preferenceRepository.asViewModelState(AppPreferences.requestTimeout)
}
