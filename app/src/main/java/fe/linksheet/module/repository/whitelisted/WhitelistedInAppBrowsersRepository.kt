package fe.linksheet.module.repository.whitelisted

import app.linksheet.feature.app.core.ActivityAppInfo
import app.linksheet.feature.app.core.ActivityAppInfoStatus
import app.linksheet.feature.backup.api.CommonImport
import app.linksheet.feature.backup.api.ExportableRepository
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.model.WhitelistedInAppBrowserExportModel
import app.linksheet.feature.backup.model.WhitelistedInAppBrowserExportModelV1
import app.linksheet.feature.backup.model.fromExportModel
import app.linksheet.feature.backup.model.toExportModel
import fe.linksheet.module.database.dao.whitelisted.WhitelistedInAppBrowsersDao
import fe.linksheet.module.database.entity.whitelisted.WhitelistedInAppBrowser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

class WhitelistedInAppBrowsersRepository(
    val dao: WhitelistedInAppBrowsersDao
) : WhitelistedBrowsersRepository, ExportableRepository<WhitelistedInAppBrowser, WhitelistedInAppBrowserExportModel> {

    override val modelClass: KClass<WhitelistedInAppBrowserExportModel>
        get() = WhitelistedInAppBrowserExportModel::class

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
        dao.insertReplace(WhitelistedInAppBrowser(packageName = flatComponentName))
    }

    suspend fun delete(flatComponentName: String) {
        dao.deleteByPackageOrComponentName(flatComponentName)
    }

    override suspend fun insertOrDelete(newState: Boolean, appInfo: ActivityAppInfo) {
        val flatCmpName = appInfo.flatComponentName
        when {
            newState -> dao.insertReplace(WhitelistedInAppBrowser(packageName = flatCmpName))
            else -> dao.deleteByPackageOrComponentName(flatCmpName)
        }
    }

    suspend fun insertOrDelete(newState: Boolean, status: ActivityAppInfoStatus) {
        insertOrDelete(newState, status.appInfo)
    }

    suspend fun deleteByPackageName(packageName: String) {
        dao.deleteByPackageOrComponentName(packageName)
    }

    override suspend fun exportAll(): List<WhitelistedInAppBrowserExportModelV1> {
        return CommonImport.export(dao) { it.toExportModel() }
    }

    override suspend fun import(
        settings: ImportSettings,
        models: List<WhitelistedInAppBrowserExportModel>
    ): List<Pair<WhitelistedInAppBrowser, Long>> {
        return CommonImport.import(dao, settings, models) { it.fromExportModel() }
    }
}
