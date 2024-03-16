package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.app.AppPreferenceRepository



import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class FollowRedirectsSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var followRedirects = preferenceRepository.asState(AppPreferences.followRedirects)
    var followRedirectsLocalCache = preferenceRepository.asState(
        AppPreferences.followRedirectsLocalCache
    )
    val followRedirectsBuiltInCache = preferenceRepository.asState(
        AppPreferences.followRedirectsBuiltInCache
    )
    var followRedirectsExternalService = preferenceRepository.asState(
        AppPreferences.followRedirectsExternalService
    )
    var followOnlyKnownTrackers = preferenceRepository.asState(
        AppPreferences.followOnlyKnownTrackers
    )
    val followRedirectsAllowsDarknets = preferenceRepository.asState(AppPreferences.followRedirectsAllowDarknets)

    val followRedirectsTimeout = preferenceRepository.asState(AppPreferences.requestTimeout)
}
