package fe.linksheet.module.viewmodel


import android.app.Application
import app.linksheet.api.preference.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class BottomSheetActionBarSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {

    val enable = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.actionBar.enable)
    val enableIgnoreLibRedirectButton = preferenceRepository.asViewModelState(AppPreferences.libRedirect.enableIgnoreLibRedirectButton)
    val hideAfterCopying = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.actionBar.hideAfterCopying)
    val enableOpenExternallyButton = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.actionBar.enableOpenExternallyButton)
    val doubleTapUrl = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.actionBar.doubleTapUrl)
}
