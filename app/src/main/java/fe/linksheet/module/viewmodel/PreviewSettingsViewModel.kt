package fe.linksheet.module.viewmodel

import app.linksheet.api.preference.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class PreviewSettingsViewModel(
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val enableActionBar = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.actionBar.enable)
    val urlPreview = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.openGraphPreview.enable)
    val urlPreviewSkipBrowser = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.openGraphPreview.skipBrowser)
    val followRedirects = preferenceRepository.asViewModelState(AppPreferences.followRedirects.enable)
    val requestTimeout = preferenceRepository.asViewModelState(AppPreferences.requestTimeout)
}
