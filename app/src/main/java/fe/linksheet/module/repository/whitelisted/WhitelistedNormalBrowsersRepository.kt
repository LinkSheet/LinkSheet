package fe.linksheet.module.repository.whitelisted

import app.linksheet.feature.app.core.ActivityAppInfo
import app.linksheet.feature.app.core.ActivityAppInfoStatus
import app.linksheet.feature.backup.api.ExportableRepository
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.model.WhitelistedNormalBrowserExportModel
import app.linksheet.feature.backup.model.fromExportModel
import app.linksheet.feature.backup.model.toExportModel
import fe.linksheet.module.database.dao.whitelisted.WhitelistedNormalBrowsersDao
import fe.linksheet.module.database.entity.whitelisted.WhitelistedNormalBrowser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

class WhitelistedNormalBrowsersRepository(
    val dao: WhitelistedNormalBrowsersDao
) : WhitelistedBrowsersRepository, ExportableRepository<WhitelistedNormalBrowserExportModel> {
    override val modelClass: KClass<WhitelistedNormalBrowserExportModel>
        get() = WhitelistedNormalBrowserExportModel::class

    fun getAll(): Flow<List<WhitelistedNormalBrowser>> {
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
        dao.insertReplace(WhitelistedNormalBrowser(packageName = flatComponentName))
    }

    suspend fun delete(flatComponentName: String) {
        dao.deleteByPackageOrComponentName(flatComponentName)
    }

    override suspend fun insertOrDelete(newState: Boolean, appInfo: ActivityAppInfo) {
        val flatCmpName = appInfo.flatComponentName
        when {
            newState -> dao.insertReplace(WhitelistedNormalBrowser(packageName = flatCmpName))
            else -> dao.deleteByPackageOrComponentName(flatCmpName)
        }
    }

    suspend fun insertOrDelete(newState: Boolean, status: ActivityAppInfoStatus) {
        insertOrDelete(newState, status.appInfo)
    }

    suspend fun deleteByPackageName(packageName: String) {
        dao.deleteByPackageOrComponentName(packageName)
    }

    override suspend fun exportAll(): List<WhitelistedNormalBrowserExportModel> {
        return dao.getAll().first().map { it.toExportModel() }
    }

    override suspend fun import(settings: ImportSettings, models: List<WhitelistedNormalBrowserExportModel>) {
        val entities = models.map { it.fromExportModel() }
        if (settings.replace) {
            dao.insertReplace(entities)
        } else {
            dao.insert(entities)
        }
    }
}
