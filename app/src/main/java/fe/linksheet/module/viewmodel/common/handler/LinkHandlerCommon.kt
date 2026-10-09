package fe.linksheet.module.viewmodel.common.handler

import app.linksheet.feature.app.core.IAppInfo
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.composable.dialog.HostState
import fe.linksheet.feature.app.toPreferredApp
import fe.linksheet.module.repository.PreferredAppRepository

class LinkHandlerCommon(
    private val preferredAppRepository: PreferredAppRepository,
) {
    suspend fun updateHostState(appInfo: IAppInfo, hostStates: List<HostState>) {
        val inserts = hostStates
            .filter { !it.previousState && it.currentState }
            .map { appInfo.toPreferredApp(it.host, true) }
        preferredAppRepository.insert(inserts)

        val deleteHosts= hostStates
            .filter { it.previousState && !it.currentState }
            .mapToSet{ it.host }
        preferredAppRepository.deleteByHostsAndPackageName(deleteHosts, appInfo.packageName)
    }
}
