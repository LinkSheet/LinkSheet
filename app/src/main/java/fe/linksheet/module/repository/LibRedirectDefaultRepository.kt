package fe.linksheet.module.repository

import fe.linksheet.data.dao.LibRedirectDefaultDao
import fe.linksheet.data.entity.LibRedirectDefault
import fe.linksheet.data.entity.LibRedirectServiceState
import kotlinx.coroutines.flow.MutableStateFlow


class LibRedirectDefaultRepository(private val dao: LibRedirectDefaultDao) {
    fun getByServiceKey(serviceKey: MutableStateFlow<String>) = getByServiceKey(serviceKey.value)
    fun getByServiceKey(serviceKey: String) = dao.getByServiceKey(serviceKey)

    suspend fun insert(default: LibRedirectDefault) = dao.insert(default)
}