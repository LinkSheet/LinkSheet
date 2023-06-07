package fe.linksheet.module.repository.whitelisted

import fe.linksheet.extension.mapToSet
import fe.linksheet.module.database.dao.base.PackageEntityCreator
import fe.linksheet.module.database.dao.base.PackageEntityDao
import fe.linksheet.module.database.dao.base.WhitelistedBrowsersDao
import fe.linksheet.module.database.entity.whitelisted.WhitelistedBrowser
import kotlinx.coroutines.flow.map


abstract class WhitelistedBrowsersRepository<T : WhitelistedBrowser<T>, C : PackageEntityCreator<T>, D : WhitelistedBrowsersDao<T, C>>(
    private val dao: D
) {
    fun getAll() = dao.getAll()

    fun getPackageSet() = getAll().map { list -> list.mapToSet { it.packageName } }

    suspend fun insertOrDelete(insert: Boolean, packageName: String) = dao.insertOrDelete(
        PackageEntityDao.Mode.fromBool(insert), packageName
    )

    suspend fun deleteByPackageName(packageName: String) = dao.deleteByPackageName(packageName)
}