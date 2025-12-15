package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class LinksSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val useClearUrls = preferenceRepository.asViewModelState(AppPreferences.useClearUrls)
    val useFastForwardRules = preferenceRepository.asViewModelState(AppPreferences.useFastForwardRules)
    val enableLibRedirect = preferenceRepository.asViewModelState(AppPreferences.libRedirect.enable)
    val followRedirects = preferenceRepository.asViewModelState(AppPreferences.followRedirects.enable)
    val enableDownloader = preferenceRepository.asViewModelState(AppPreferences.downloader.enable)
    val enableAmp2Html = preferenceRepository.asViewModelState(AppPreferences.amp2Html.enable)
    val urlPreview = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.openGraphPreview.enable)
    val resolveEmbeds = preferenceRepository.asViewModelState(AppPreferences.resolveEmbeds)
}
