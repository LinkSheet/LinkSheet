package fe.linksheet.module.repository

import android.net.Uri
import app.linksheet.feature.backup.api.ExportableRepository
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.model.AppSelectionHistoryExportModel
import app.linksheet.feature.backup.model.fromExportModel
import app.linksheet.feature.backup.model.toExportModel
import fe.linksheet.module.database.dao.AppSelectionHistoryDao
import fe.linksheet.module.database.entity.AppSelectionHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlin.reflect.KClass


class AppSelectionHistoryRepository(
    private val dao: AppSelectionHistoryDao
) : ExportableRepository<AppSelectionHistoryExportModel> {

    override val modelClass: KClass<AppSelectionHistoryExportModel>
        get() = AppSelectionHistoryExportModel::class

    suspend fun insert(appSelectionHistory: AppSelectionHistory) = dao.insertReplace(appSelectionHistory)
    suspend fun insert(appSelectionHistories: List<AppSelectionHistory>) = dao.insertReplace(appSelectionHistories)

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
        return dao.getAll().first().map { it.toExportModel() }
    }

    override suspend fun import(settings: ImportSettings, models: List<AppSelectionHistoryExportModel>) {
        val entities = models.map { it.fromExportModel() }
        if (settings.replace) {
            dao.insertReplace(entities)
        } else {
            dao.insert(entities)
        }
    }
}
