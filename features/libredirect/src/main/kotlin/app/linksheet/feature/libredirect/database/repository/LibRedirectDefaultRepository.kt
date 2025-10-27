package app.linksheet.feature.libredirect.database.repository

import app.linksheet.feature.libredirect.database.dao.LibRedirectDefaultDao
import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull


class LibRedirectDefaultRepository(private val dao: LibRedirectDefaultDao) {
    fun getByServiceKey(serviceKey: String): Flow<LibRedirectDefault?> = dao.getByServiceKey(serviceKey)

    suspend fun getInstanceUrl(serviceKey: String) = getByServiceKey(serviceKey).firstOrNull()?.instanceUrl

    suspend fun insert(default: LibRedirectDefault) = dao.insert(default)

    suspend fun insert(service: String, frontend: String, instance: String) {
        dao.insert(LibRedirectDefault(service, frontend, instance))
    }

    suspend fun delete(default: LibRedirectDefault) {
        dao.delete(default)
    }
}
