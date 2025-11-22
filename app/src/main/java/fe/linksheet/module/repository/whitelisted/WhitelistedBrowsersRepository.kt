package fe.linksheet.module.repository.whitelisted

import fe.linksheet.module.database.dao.base.PackageEntityCreator
import fe.linksheet.module.database.dao.base.PackageEntityDao
import fe.linksheet.module.database.dao.base.WhitelistedBrowsersDao
import fe.linksheet.module.database.entity.whitelisted.WhitelistedBrowser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


abstract class WhitelistedBrowsersRepository<T : WhitelistedBrowser<T>, C : PackageEntityCreator<T>, D : WhitelistedBrowsersDao<T, C>>(
    private val dao: D
) {
    fun getAll(): Flow<List<T>> {
        return dao.getAll()
    }

    fun getPackageSet(): Flow<WhitelistedBrowserInfo> {
        return getAll().map { list -> createWhitelistedBrowserInfo(list.map { it.packageName }) }
    }

    suspend fun insertOrDelete(insert: Boolean, flatComponentName: String) {
        dao.insertOrDelete(PackageEntityDao.Mode.fromBool(insert), flatComponentName)
    }

    suspend fun deleteByPackageName(packageName: String) = dao.deleteByFlatComponentName(packageName)
}
