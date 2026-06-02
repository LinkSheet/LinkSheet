package fe.linksheet.module.repository

import android.net.Uri
import app.linksheet.feature.backup.api.ExportableRepository
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.model.PreferredAppExportModel
import app.linksheet.feature.backup.model.fromExportModel
import app.linksheet.feature.backup.model.toExportModel
import fe.linksheet.module.database.dao.PreferredAppDao
import fe.linksheet.module.database.entity.PreferredApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlin.reflect.KClass

class PreferredAppRepository(
    private val dao: PreferredAppDao
) : ExportableRepository<PreferredAppExportModel> {

    override val modelClass: KClass<PreferredAppExportModel>
        get() = PreferredAppExportModel::class

    override suspend fun exportAll(): List<PreferredAppExportModel> {
        return dao.getAll().first().map { it.toExportModel() }
    }

    override suspend fun import(settings: ImportSettings, models: List<PreferredAppExportModel>) {
        val entities = models.map { it.fromExportModel() }
        if (settings.replace) {
            dao.insertReplace(entities)
        } else {
            dao.insert(entities)
        }
    }

    fun getAll(): Flow<List<PreferredApp>> {
        return dao.getAll()
    }

    fun getAllAlwaysPreferred() = dao.getAllAlwaysPreferred()

    suspend fun getByHost(uri: Uri?): PreferredApp? {
        if (uri?.host == null) return null
        return dao.getByHost(uri.host!!).firstOrNull()
    }


    suspend fun deleteByPackageName(packageName: String) = dao.deleteByPackageName(packageName)

    suspend fun delete(preferredApp: PreferredApp) {
        dao.delete(preferredApp)
    }

    suspend fun deleteByPackageNames(packageNames: Set<String>) =
        dao.deleteByPackageName(packageNames)

    suspend fun deleteByHostAndPackageName(
        host: String,
        packageName: String,
    ) = dao.deleteByHostAndPackageName(host, packageName)

    suspend fun deleteByHost(host: String) {
        dao.deleteByHost(host)
    }

    suspend fun insert(preferredApp: PreferredApp) {
        dao.insertReplace(preferredApp)
    }

    suspend fun insert(items: List<PreferredApp>) {
        dao.insertReplace(items)
    }

    fun getByPackageNameFlow(packageName: String): Flow<List<PreferredApp>> {
        return dao.getByPackageName(packageName)
    }

    suspend fun getByPackageName(packageName: String): List<PreferredApp> {
        return dao.getByPackageName(packageName).first()
    }
}
