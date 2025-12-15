package fe.linksheet.module.viewmodel


import android.content.ComponentName
import androidx.lifecycle.viewModelScope
import app.linksheet.feature.app.core.ActivityAppInfo
import app.linksheet.feature.app.usecase.BrowsersUseCase
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PreferredBrowserViewModel(
    val browserPackageService: BrowsersUseCase,
    preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {

    val type = MutableStateFlow(BrowserType.Normal)
    val autoLaunchSingleBrowser = preferenceRepository.asViewModelState(AppPreferences.browserMode.autoLaunchSingleBrowser)
    val unifiedPreferredBrowser = preferenceRepository.asViewModelState(AppPreferences.browserMode.unifiedPreferredBrowser)

    fun init() {
        viewModelScope.launch {
            unifiedPreferredBrowser.stateFlow
                .map { it }
                .collect { type.emit(BrowserType.Normal) }
        }
    }

    private val normalBrowserMode = preferenceRepository.asViewModelState(AppPreferences.browserMode.browserMode)
    private val inAppBrowserMode = preferenceRepository.asViewModelState(AppPreferences.browserMode.inAppBrowserMode)

    val browserMode = type.map {
        when (it) {
            BrowserType.Normal -> normalBrowserMode
            BrowserType.InApp -> inAppBrowserMode
        }
    }

    @OptIn(SensitivePreference::class)
    private val selectedNormalBrowser = preferenceRepository.asViewModelState(AppPreferences.browserMode.selectedBrowser)

    @OptIn(SensitivePreference::class)
    private val selectedInAppBrowser = preferenceRepository.asViewModelState(AppPreferences.browserMode.selectedInAppBrowser)

    val selectedBrowser = type.map {
        when (it) {
            BrowserType.Normal -> selectedNormalBrowser
            BrowserType.InApp -> selectedInAppBrowser
        }
    }

    fun getAppInfo(state: String?): ActivityAppInfo? {
        if (state == null) return null
        val cmp = ComponentName.unflattenFromString(state)
        val packageName = cmp?.packageName ?: state
        return browserPackageService.queryBrowser(packageName)
    }


    enum class BrowserType {
        Normal, InApp
    }
}
