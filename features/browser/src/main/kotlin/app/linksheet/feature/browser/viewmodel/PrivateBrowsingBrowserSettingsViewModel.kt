package app.linksheet.feature.browser.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.linksheet.feature.app.applist.AppListCommon
import app.linksheet.feature.app.core.ActivityAppInfo
import app.linksheet.feature.app.usecase.BrowsersUseCase
import app.linksheet.feature.browser.core.PrivateBrowsingService
import app.linksheet.feature.browser.database.repository.PrivateBrowsingBrowserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PrivateBrowsingBrowserSettingsViewModel internal constructor(
    private val repository: PrivateBrowsingBrowserRepository,
    private val useCase: BrowsersUseCase,
    private val privateBrowsingService: PrivateBrowsingService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    val list by lazy {
        AppListCommon(
            apps = useCase.queryBrowsersFlow().map { list ->
                list.filter { privateBrowsingService.isKnownBrowser(it.packageName, true) != null }
            },
            scope = viewModelScope
        )
    }

    val all = repository.getAll().map { list ->
        list.mapTo(LinkedHashSet()) { it.flatComponentName }
    }

    fun save(app: ActivityAppInfo, enabled: Boolean) = viewModelScope.launch(dispatcher) {
        if (enabled) {
            repository.insert(app.flatComponentName)
        } else {
            repository.deleteByFlatComponentName(app.flatComponentName)
        }
    }
}
