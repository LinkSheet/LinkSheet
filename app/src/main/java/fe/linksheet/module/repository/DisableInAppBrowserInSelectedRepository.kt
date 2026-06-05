package fe.linksheet.module.repository

import app.linksheet.feature.backup.api.CommonImport
import app.linksheet.feature.backup.api.ExportableRepository
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.model.DisableInAppBrowserInSelectedExportModel
import app.linksheet.feature.backup.model.fromExportModel
import app.linksheet.feature.backup.model.toExportModel
import fe.linksheet.module.database.dao.DisableInAppBrowserInSelectedDao
import fe.linksheet.module.database.entity.DisableInAppBrowserInSelected
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass


class DisableInAppBrowserInSelectedRepository(
    private val dao: DisableInAppBrowserInSelectedDao
) : ExportableRepository<DisableInAppBrowserInSelected, DisableInAppBrowserInSelectedExportModel> {

    override val modelClass: KClass<DisableInAppBrowserInSelectedExportModel>
        get() = DisableInAppBrowserInSelectedExportModel::class

    fun getAll(): Flow<List<DisableInAppBrowserInSelected>> {
        return dao.getAll()
    }

    suspend fun insert(flatComponentName: String) {
        dao.insertReplace(DisableInAppBrowserInSelected(packageName = flatComponentName))
    }

    suspend fun delete(flatComponentName: String) {
        dao.deleteByPackageOrComponentName(flatComponentName)
    }

    suspend fun insertOrDelete(insert: Boolean, flatComponentName: String) {
        when {
            insert -> dao.insertReplace(DisableInAppBrowserInSelected(packageName = flatComponentName))
            else -> dao.deleteByPackageOrComponentName(flatComponentName)
        }
    }

    override suspend fun exportAll(): List<DisableInAppBrowserInSelectedExportModel> {
        return CommonImport.export(dao) { it.toExportModel() }
    }

    override suspend fun import(
        settings: ImportSettings,
        models: List<DisableInAppBrowserInSelectedExportModel>
    ): List<Pair<DisableInAppBrowserInSelected, Long>> {
        return CommonImport.import(dao, settings, models) { it.fromExportModel() }
    }
}
