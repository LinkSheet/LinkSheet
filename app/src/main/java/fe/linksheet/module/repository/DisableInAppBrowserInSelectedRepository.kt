package fe.linksheet.module.repository

import fe.linksheet.data.dao.DisableInAppBrowserInSelectedDao
import fe.linksheet.data.dao.base.PackageEntityDao


class DisableInAppBrowserInSelectedRepository(val dao: DisableInAppBrowserInSelectedDao) {
    fun getAll() = dao.getAll()

    suspend fun insertOrDelete(insert: Boolean, packageName: String) = dao.insertOrDelete(
        PackageEntityDao.Mode.fromBool(insert), packageName
    )
}