package fe.linksheet.module.repository

import fe.linksheet.data.dao.LibRedirectServiceStateDao
import fe.linksheet.data.entity.LibRedirectServiceState


class LibRedirectStateRepository(private val dao: LibRedirectServiceStateDao) {
    fun getServiceState(serviceKey: String) = dao.getServiceState(serviceKey)

    suspend fun insert(state: LibRedirectServiceState) = dao.insert(state)
}