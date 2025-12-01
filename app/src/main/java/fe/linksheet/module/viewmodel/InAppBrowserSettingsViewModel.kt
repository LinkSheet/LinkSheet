package fe.linksheet.module.viewmodel


import androidx.lifecycle.viewModelScope
import app.linksheet.feature.app.AppInfo
import app.linksheet.feature.app.usecase.AllAppsUseCase
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.repository.DisableInAppBrowserInSelectedRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.module.viewmodel.common.AppListCommon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class InAppBrowserSettingsViewModel(
    private val repository: DisableInAppBrowserInSelectedRepository,
    private val appPackageService: AllAppsUseCase,
    preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {

    val list by lazy { AppListCommon(apps = appPackageService.queryAllAppsFlow(), scope = viewModelScope) }
    val inAppBrowserMode = preferenceRepository.asViewModelState(AppPreferences.inAppBrowserSettings)

    val disabledPackages = repository.getAll().map { list ->
        list.mapToSet { it.packageName }
    }

    fun save(app: AppInfo, enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertOrDelete(enabled, app.packageName)
    }
}
