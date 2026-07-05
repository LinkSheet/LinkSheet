package fe.linksheet.module.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.linksheet.feature.app.core.IAppInfo
import app.linksheet.feature.app.usecase.AllAppsUseCase
import app.linksheet.feature.app.usecase.AppInfoWithHosts
import fe.linksheet.composable.dialog.HostState
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.viewmodel.common.handler.LinkHandlerCommon
import kotlinx.coroutines.launch

class SelectDomainsConfirmationViewModel(
    private val preferredAppRepository: PreferredAppRepository,
    private val useCase: AllAppsUseCase,
) : ViewModel() {
    private val handler by lazy {
        LinkHandlerCommon(
            preferredAppRepository = preferredAppRepository,
        )
    }

    fun updateHostState(appInfo: IAppInfo, hostStates: List<HostState>) = viewModelScope.launch {
        handler.updateHostState(appInfo, hostStates)
    }

    fun getAppInfoWithHosts(packageName: String): AppInfoWithHosts? {
        val appInfoWithHosts = useCase.getAppInfoWithHosts(packageName)
        return appInfoWithHosts
    }
}
