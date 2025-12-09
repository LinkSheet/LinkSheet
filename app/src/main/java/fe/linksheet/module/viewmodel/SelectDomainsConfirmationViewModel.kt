package fe.linksheet.module.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.linksheet.feature.app.AppInfo
import app.linksheet.feature.app.usecase.AllAppsUseCase
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.viewmodel.common.handler.LinkHandlerCommon

class SelectDomainsConfirmationViewModel(
    private val preferredAppRepository: PreferredAppRepository,
    private val useCase: AllAppsUseCase,
) : ViewModel() {
    val handler by lazy { LinkHandlerCommon(preferredAppRepository = preferredAppRepository, scope = viewModelScope) }

    fun getApp(packageName: String): Pair<AppInfo, List<String>>? {
        val app = useCase.queryApp(packageName) ?: return null
        val hosts = useCase.getSupportedHosts(packageName)

        return app to hosts
    }
}
