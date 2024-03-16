package fe.linksheet.module.repository

import fe.libredirectkt.LibRedirectFrontend
import fe.libredirectkt.LibRedirectService
import fe.linksheet.module.database.dao.LibRedirectDefaultDao
import fe.linksheet.module.database.entity.LibRedirectDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull


class LibRedirectDefaultRepository(private val dao: LibRedirectDefaultDao) {
    fun getByServiceKeyFlow(serviceKey: MutableStateFlow<String>) = getByServiceKeyFlow(serviceKey.value)
    fun getByServiceKeyFlow(serviceKey: String): Flow<LibRedirectDefault?> = dao.getByServiceKey(serviceKey)

    suspend fun getInstanceUrl(serviceKey: String) = getByServiceKeyFlow(serviceKey).firstOrNull()?.instanceUrl

    suspend fun insert(default: LibRedirectDefault) = dao.insert(default)

    suspend fun insert(service: String, frontend: String, instance: String) {
        dao.insert(LibRedirectDefault(service, frontend, instance))
    }

    suspend fun delete(default: LibRedirectDefault) {
        dao.delete(default)
    }
}
