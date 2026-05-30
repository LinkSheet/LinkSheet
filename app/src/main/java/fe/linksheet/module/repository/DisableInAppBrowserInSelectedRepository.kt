package fe.linksheet.module.repository

import fe.linksheet.module.database.dao.DisableInAppBrowserInSelectedDao
import fe.linksheet.module.database.entity.DisableInAppBrowserInSelected
import kotlinx.coroutines.flow.Flow


class DisableInAppBrowserInSelectedRepository(private val dao: DisableInAppBrowserInSelectedDao) {
    fun getAll(): Flow<List<DisableInAppBrowserInSelected>> {
        return dao.getAll()
    }

    suspend fun insert(flatComponentName: String) {
        dao.insert(DisableInAppBrowserInSelected(packageName = flatComponentName))
    }

    suspend fun delete(flatComponentName: String) {
        dao.deleteByPackageOrComponentName(flatComponentName)
    }

    suspend fun insertOrDelete(insert: Boolean, flatComponentName: String) {
        when {
            insert -> dao.insert(DisableInAppBrowserInSelected(packageName = flatComponentName))
            else -> dao.deleteByPackageOrComponentName(flatComponentName)
        }
    }
}
