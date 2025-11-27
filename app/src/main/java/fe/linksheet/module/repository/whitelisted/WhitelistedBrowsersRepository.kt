package fe.linksheet.module.repository.whitelisted

import app.linksheet.feature.app.ActivityAppInfoStatus
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

    suspend fun migrateState(items: List<ActivityAppInfoStatus>) {
        for (status in items) {
            // Only package name is stored, if enabled -> store component name instead
            if (status.isSourcePackageNameOnly) {
                if (status.enabled) {
                    dao.insert(status.appInfo.flatComponentName)
                }

                // Get rid of package name only entry
                dao.delete(status.appInfo.packageName)
            }
        }
    }

    suspend fun insertOrDelete(newState: Boolean, status: ActivityAppInfoStatus) {
        dao.insertOrDelete(PackageEntityDao.Mode.fromBool(newState), status.appInfo.flatComponentName)
    }

    suspend fun deleteByPackageName(packageName: String) = dao.deleteByPackageOrComponentName(packageName)
}
