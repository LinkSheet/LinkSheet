package fe.linksheet.module.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.linksheet.feature.app.usecase.AllAppsUseCase
import app.linksheet.feature.app.usecase.AppInfoWithHosts
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.viewmodel.common.handler.LinkHandlerCommon

class SelectDomainsConfirmationViewModel(
    private val preferredAppRepository: PreferredAppRepository,
    private val useCase: AllAppsUseCase,
) : ViewModel() {
    val handler by lazy { LinkHandlerCommon(preferredAppRepository = preferredAppRepository, scope = viewModelScope) }

    fun getAppInfoWithHosts(packageName: String): AppInfoWithHosts? {
        val appInfoWithHosts = useCase.getAppInfoWithHosts(packageName)
        return appInfoWithHosts
    }
}
