package fe.linksheet.module.repository

import android.net.Uri
import app.linksheet.feature.backup.api.CommonImport
import app.linksheet.feature.backup.api.ExportableRepository
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.model.AppSelectionHistoryExportModel
import app.linksheet.feature.backup.model.fromExportModel
import app.linksheet.feature.backup.model.toExportModel
import fe.linksheet.module.database.dao.AppSelectionHistoryDao
import fe.linksheet.module.database.entity.AppSelectionHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.reflect.KClass


class AppSelectionHistoryRepository(
    private val dao: AppSelectionHistoryDao
) : ExportableRepository<AppSelectionHistory, AppSelectionHistoryExportModel> {

    override val modelClass: KClass<AppSelectionHistoryExportModel>
        get() = AppSelectionHistoryExportModel::class

    suspend fun insert(appSelectionHistory: AppSelectionHistory): Long {
        return dao.insertReplace(appSelectionHistory)
    }

    suspend fun insert(appSelectionHistories: List<AppSelectionHistory>): List<Long> {
        return dao.insertReplace(appSelectionHistories)
    }

    suspend fun getLastUsedForHostGroupedByPackage(uri: Uri?): Map<String, Long>? {
        val host = uri?.host ?: return null
        val appSelections = dao.getLastUsedForHostGroupedByPackage(host).firstOrNull() ?: return null

        return appSelections.associate { it.packageName to it.maxLastUsed }
    }

    suspend fun delete(packageNames: List<String>) {
        dao.deleteByPackageNames(packageNames)
    }

    fun getAll(): Flow<List<AppSelectionHistory>> {
        return dao.getAll()
    }

    override suspend fun exportAll(): List<AppSelectionHistoryExportModel> {
        return CommonImport.export(dao) { it.toExportModel() }
    }

    override suspend fun eraseAll() {
        dao.deleteAll()
    }

    override suspend fun import(
        settings: ImportSettings,
        models: List<AppSelectionHistoryExportModel>
    ): List<Pair<AppSelectionHistory, Long>> {
        return CommonImport.import(dao, settings, models) { it.fromExportModel() }
    }
}
