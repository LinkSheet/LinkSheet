package fe.linksheet.module.repository.whitelisted

import app.linksheet.feature.app.core.ActivityAppInfo
import app.linksheet.feature.app.core.ActivityAppInfoStatus
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
            migrateState(status.appInfo, status.enabled, status.isSourcePackageNameOnly)
        }
    }

    suspend fun migrateState(appInfo: ActivityAppInfo, enabled: Boolean, isSourcePackageNameOnly: Boolean) {
        // Only package name is stored, if enabled -> store component name instead
        if (isSourcePackageNameOnly) {
            if (enabled) {
                dao.insert(appInfo.flatComponentName)
            }

            // Get rid of package name only entry
            dao.delete(appInfo.packageName)
        }
    }
    suspend fun insertOrDelete(newState: Boolean, appInfo: ActivityAppInfo) {
        dao.insertOrDelete(PackageEntityDao.Mode.fromBool(newState), appInfo.flatComponentName)
    }
    suspend fun insertOrDelete(newState: Boolean, status: ActivityAppInfoStatus) {
        insertOrDelete(newState, status.appInfo)
    }

    suspend fun deleteByPackageName(packageName: String) = dao.deleteByPackageOrComponentName(packageName)
}
