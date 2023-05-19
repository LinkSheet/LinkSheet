package fe.linksheet.module.repository

import fe.linksheet.data.dao.WhitelistedBrowsersDao
import fe.linksheet.data.dao.base.PackageEntityDao


class WhitelistedBrowserRepository(private val dao: WhitelistedBrowsersDao) {
    fun getAll() = dao.getAll()

    suspend fun insertOrDelete(insert: Boolean, packageName: String) = dao.insertOrDelete(
        PackageEntityDao.Mode.fromBool(insert), packageName
    )

    suspend fun deleteByPackageName(packageName: String) = dao.deleteByPackageName(packageName)
}