package fe.linksheet.module.viewmodel.common.handler

import app.linksheet.feature.app.core.IAppInfo
import fe.linksheet.composable.dialog.HostState
import fe.linksheet.feature.app.toPreferredApp
import fe.linksheet.module.repository.PreferredAppRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LinkHandlerCommon(
    private val preferredAppRepository: PreferredAppRepository,
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun updateHostState(appInfo: IAppInfo, hostStates: List<HostState>) = scope.launch(dispatcher) {
        for ((host, previousState, currentState) in hostStates) {
            when {
                previousState && !currentState -> {
                    preferredAppRepository.deleteByHostAndPackageName(host, appInfo.packageName)
                }

                !previousState && currentState -> {
                    preferredAppRepository.insert(appInfo.toPreferredApp(host, true))
                }
            }
        }
    }
}
