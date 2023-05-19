package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.Preferences

class LinksSettingsViewModel(
    val context: Application,
    preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var useClearUrls = preferenceRepository.getBooleanState(Preferences.useClearUrls)
    var useFastForwardRules = preferenceRepository.getBooleanState(Preferences.useFastForwardRules)
    var enableLibRedirect = preferenceRepository.getBooleanState(Preferences.enableLibRedirect)
    var followRedirects = preferenceRepository.getBooleanState(Preferences.followRedirects)
    var followRedirectsLocalCache =
        preferenceRepository.getBooleanState(Preferences.followRedirectsLocalCache)
    var followRedirectsExternalService =
        preferenceRepository.getBooleanState(Preferences.followRedirectsExternalService)
    var followOnlyKnownTrackers =
        preferenceRepository.getBooleanState(Preferences.followOnlyKnownTrackers)
    var enableDownloader = preferenceRepository.getBooleanState(Preferences.enableDownloader)
    var downloaderCheckUrlMimeType =
        preferenceRepository.getBooleanState(Preferences.downloaderCheckUrlMimeType)
}