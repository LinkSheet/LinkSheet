package app.linksheet.feature.libredirect.database.repository

import app.linksheet.feature.libredirect.database.dao.LibRedirectUserInstanceDao
import app.linksheet.feature.libredirect.database.entity.LibRedirectUserInstance
import kotlinx.coroutines.flow.Flow

class LibRedirectUserInstanceRepository(private val dao: LibRedirectUserInstanceDao) {
    fun getByServiceAndFrontendOrNull(serviceKey: String, frontend: String): Flow<List<LibRedirectUserInstance>> {
        return dao.getByServiceAndFrontendOrNull(serviceKey, frontend)
    }

    suspend fun insert(userInstance: LibRedirectUserInstance) {
        dao.insert(userInstance)
    }

    suspend fun delete(userInstance: LibRedirectUserInstance) {
        dao.delete(userInstance)
    }
}
