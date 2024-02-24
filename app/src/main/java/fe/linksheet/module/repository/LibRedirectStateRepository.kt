package fe.linksheet.module.repository

import fe.linksheet.module.database.dao.LibRedirectServiceStateDao
import fe.linksheet.module.database.entity.LibRedirectServiceState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class LibRedirectStateRepository(private val dao: LibRedirectServiceStateDao) {
    private fun getServiceState(serviceKey: String) = dao.getServiceState(serviceKey)
    fun isEnabledFlow(serviceKey: String) = getServiceState(serviceKey).map { it?.enabled ?: false }
    suspend fun isEnabled(serviceKey: String) = isEnabledFlow(serviceKey).first()

    suspend fun insert(state: LibRedirectServiceState) = dao.insert(state)
}
