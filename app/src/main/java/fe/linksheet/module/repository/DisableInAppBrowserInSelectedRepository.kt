package fe.linksheet.module.repository

import fe.linksheet.module.database.dao.DisableInAppBrowserInSelectedDao
import fe.linksheet.module.database.dao.base.PackageEntityDao


class DisableInAppBrowserInSelectedRepository(private val dao: DisableInAppBrowserInSelectedDao) {
    fun getAll() = dao.getAll()

    suspend fun insertOrDelete(insert: Boolean, packageName: String) = dao.insertOrDelete(
        PackageEntityDao.Mode.fromBool(insert), packageName
    )
}