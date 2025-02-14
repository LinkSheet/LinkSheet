package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.app.AppPreferenceRepository



import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class FollowRedirectsSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val followRedirects = preferenceRepository.asState(AppPreferences.followRedirects)
    val followRedirectsLocalCache = preferenceRepository.asState(
        AppPreferences.followRedirectsLocalCache
    )

    val followRedirectsExternalService = preferenceRepository.asState(
        AppPreferences.followRedirectsExternalService
    )
    val followOnlyKnownTrackers = preferenceRepository.asState(
        AppPreferences.followOnlyKnownTrackers
    )
    val followRedirectsAllowsDarknets = preferenceRepository.asState(AppPreferences.followRedirectsAllowDarknets)
    val followRedirectsSkipBrowser = preferenceRepository.asState(AppPreferences.followRedirectsSkipBrowser)

    val followRedirectsTimeout = preferenceRepository.asState(AppPreferences.requestTimeout)
}
