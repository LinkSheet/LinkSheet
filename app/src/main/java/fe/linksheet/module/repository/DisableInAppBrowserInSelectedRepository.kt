package fe.linksheet.module.repository

import fe.linksheet.module.database.dao.DisableInAppBrowserInSelectedDao
import fe.linksheet.module.database.dao.base.PackageEntityDao
import fe.linksheet.module.database.entity.DisableInAppBrowserInSelected
import kotlinx.coroutines.flow.Flow


class DisableInAppBrowserInSelectedRepository(private val dao: DisableInAppBrowserInSelectedDao) {
    fun getAll(): Flow<List<DisableInAppBrowserInSelected>> {
        return dao.getAll()
    }

    suspend fun insertOrDelete(insert: Boolean, packageName: String) = dao.insertOrDelete(
        PackageEntityDao.Mode.fromBool(insert), packageName
    )
}
