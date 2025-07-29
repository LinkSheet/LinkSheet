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
    val enableLibRedirect = preferenceRepository.asViewModelState(AppPreferences.enableLibRedirect)
    val followRedirects = preferenceRepository.asViewModelState(AppPreferences.followRedirects)
    val enableDownloader = preferenceRepository.asViewModelState(AppPreferences.enableDownloader)
    val enableAmp2Html = preferenceRepository.asViewModelState(AppPreferences.enableAmp2Html)
    val urlPreview = preferenceRepository.asViewModelState(AppPreferences.urlPreview)
    val resolveEmbeds = preferenceRepository.asViewModelState(AppPreferences.resolveEmbeds)
}
