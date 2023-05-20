package fe.linksheet.module.repository

import fe.linksheet.module.database.dao.LibRedirectDefaultDao
import fe.linksheet.module.database.entity.LibRedirectDefault
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull


class LibRedirectDefaultRepository(private val dao: LibRedirectDefaultDao) {
    fun getByServiceKeyFlow(serviceKey: MutableStateFlow<String>) = getByServiceKeyFlow(serviceKey.value)
    fun getByServiceKeyFlow(serviceKey: String) = dao.getByServiceKey(serviceKey)

    suspend fun getInstanceUrl(serviceKey: String) = getByServiceKeyFlow(serviceKey).firstOrNull()?.instanceUrl

    suspend fun insert(default: LibRedirectDefault) = dao.insert(default)
}