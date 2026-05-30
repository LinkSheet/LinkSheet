package fe.linksheet.module.repository.whitelisted

import app.linksheet.feature.app.core.ActivityAppInfo
import app.linksheet.feature.app.core.ActivityAppInfoStatus
import fe.linksheet.module.database.dao.whitelisted.WhitelistedInAppBrowsersDao
import fe.linksheet.module.database.entity.whitelisted.WhitelistedInAppBrowser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WhitelistedInAppBrowsersRepository(val dao: WhitelistedInAppBrowsersDao) : WhitelistedBrowsersRepository {

    fun getAll(): Flow<List<WhitelistedInAppBrowser>> {
        return dao.getAll()
    }

    override fun getPackageSet(): Flow<WhitelistedBrowserInfo> {
        return getAll().map { list -> createWhitelistedBrowserInfo(list.map { it.packageName }) }
    }

    override suspend fun migrateState(items: List<ActivityAppInfoStatus>) {
        for (status in items) {
            migrateState(status.appInfo, status.enabled, status.isSourcePackageNameOnly)
        }
    }

    override suspend fun migrateState(
        appInfo: ActivityAppInfo,
        enabled: Boolean,
        isSourcePackageNameOnly: Boolean
    ) {
        // Only package name is stored, if enabled -> store component name instead
        if (isSourcePackageNameOnly) {
            if (enabled) {
                insert(appInfo.flatComponentName)
            }

            // Get rid of package name only entry
            delete(appInfo.packageName)
        }
    }

    suspend fun insert(flatComponentName: String) {
        dao.insert(WhitelistedInAppBrowser(packageName = flatComponentName))
    }

    suspend fun delete(flatComponentName: String) {
        dao.deleteByPackageOrComponentName(flatComponentName)
    }

    override suspend fun insertOrDelete(newState: Boolean, appInfo: ActivityAppInfo) {
        val flatCmpName = appInfo.flatComponentName
        when {
            newState -> dao.insert(WhitelistedInAppBrowser(packageName = flatCmpName))
            else -> dao.deleteByPackageOrComponentName(flatCmpName)
        }
    }

    suspend fun insertOrDelete(newState: Boolean, status: ActivityAppInfoStatus) {
        insertOrDelete(newState, status.appInfo)
    }

    suspend fun deleteByPackageName(packageName: String) {
        dao.deleteByPackageOrComponentName(packageName)
    }
}
