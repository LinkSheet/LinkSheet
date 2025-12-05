package fe.linksheet.module.viewmodel

import androidx.lifecycle.viewModelScope
import app.linksheet.feature.app.ActivityAppInfo
import app.linksheet.feature.app.usecase.BrowsersUseCase
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.module.viewmodel.common.applist.AppListCommon

class SingleBrowserViewModel(
    val type: PreferredBrowserViewModel.BrowserType,
    val useCase: BrowsersUseCase,
    preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {

    val list by lazy { AppListCommon(apps = useCase.queryBrowsersFlow(), scope = viewModelScope) }

    private val normalBrowserMode = preferenceRepository.asViewModelState(AppPreferences.browserMode)
    private val inAppBrowserMode = preferenceRepository.asViewModelState(AppPreferences.inAppBrowserMode)

    val browserMode = when (type) {
        PreferredBrowserViewModel.BrowserType.Normal -> normalBrowserMode
        PreferredBrowserViewModel.BrowserType.InApp -> inAppBrowserMode
    }

    @OptIn(SensitivePreference::class)
    private val selectedNormalBrowser = preferenceRepository.asViewModelState(AppPreferences.selectedBrowser)
    @OptIn(SensitivePreference::class)
    private val selectedInAppBrowser = preferenceRepository.asViewModelState(AppPreferences.selectedInAppBrowser)

    val selectedBrowser = when (type) {
        PreferredBrowserViewModel.BrowserType.Normal -> selectedNormalBrowser
        PreferredBrowserViewModel.BrowserType.InApp -> selectedInAppBrowser
    }

    fun updateSelectedBrowser(selectedBrowserPackage: ActivityAppInfo) {
        browserMode(BrowserMode.SelectedBrowser)
        selectedBrowser(selectedBrowserPackage.flatComponentName)
    }
}
