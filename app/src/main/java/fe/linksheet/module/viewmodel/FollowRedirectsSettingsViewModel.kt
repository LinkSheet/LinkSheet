package fe.linksheet.module.viewmodel

import android.app.Application
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.android.preference.helper.compose.getIntState
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class FollowRedirectsSettingsViewModel(
    val context: Application,
    preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var followRedirects = preferenceRepository.getBooleanState(Preferences.followRedirects)
    var followRedirectsLocalCache = preferenceRepository.getBooleanState(
        Preferences.followRedirectsLocalCache
    )
    var followRedirectsExternalService = preferenceRepository.getBooleanState(
        Preferences.followRedirectsExternalService
    )
    var followOnlyKnownTrackers = preferenceRepository.getBooleanState(
        Preferences.followOnlyKnownTrackers
    )
    val followRedirectsTimeout = preferenceRepository.getIntState(Preferences.requestTimeout)
}