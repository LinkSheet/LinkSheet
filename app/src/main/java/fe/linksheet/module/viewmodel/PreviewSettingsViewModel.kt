package fe.linksheet.module.viewmodel

import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class PreviewSettingsViewModel(
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val urlPreview = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.openGraphPreview.enable)
    val urlPreviewSkipBrowser = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.openGraphPreview.skipBrowser)
    val followRedirects = preferenceRepository.asViewModelState(AppPreferences.followRedirects.enable)
    val followRedirectsLocalCache = preferenceRepository.asViewModelState(AppPreferences.followRedirects.localCache)
    val followRedirectsExternalService = preferenceRepository.asViewModelState(AppPreferences.followRedirects.externalService)
    val followOnlyKnownTrackers = preferenceRepository.asViewModelState(AppPreferences.followRedirects.onlyKnownTrackers)
    val followRedirectsAllowsDarknets = preferenceRepository.asViewModelState(AppPreferences.followRedirects.allowDarknets)
    val followRedirectsAllowLocalNetwork = preferenceRepository.asViewModelState(AppPreferences.followRedirects.allowLocalNetwork)
    val followRedirectsSkipBrowser = preferenceRepository.asViewModelState(AppPreferences.followRedirects.skipBrowser)
    val requestTimeout = preferenceRepository.asViewModelState(AppPreferences.requestTimeout)
}
