package app.linksheet.feature.libredirect.database.repository

import app.linksheet.feature.libredirect.database.dao.LibRedirectServiceStateDao
import app.linksheet.feature.libredirect.database.entity.LibRedirectServiceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map


class LibRedirectStateRepository(private val dao: LibRedirectServiceStateDao) {
    private fun getServiceState(serviceKey: String): Flow<LibRedirectServiceState?> {
        return dao.getServiceState(serviceKey)
    }

    fun isEnabledFlow(serviceKey: String): Flow<Boolean> {
        return getServiceState(serviceKey).map { it?.enabled ?: false }
    }

    suspend fun isEnabled(serviceKey: String) = isEnabledFlow(serviceKey).firstOrNull() == true

    suspend fun insert(state: LibRedirectServiceState) = dao.insert(state)
}
