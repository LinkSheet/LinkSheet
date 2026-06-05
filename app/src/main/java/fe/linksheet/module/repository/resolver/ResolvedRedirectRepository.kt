package fe.linksheet.module.repository.resolver

import app.linksheet.feature.backup.api.CommonImport
import app.linksheet.feature.backup.api.ExportableRepository
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.model.ResolvedRedirectExportModel
import app.linksheet.feature.backup.model.fromExportModel
import app.linksheet.feature.backup.model.toExportModel
import fe.linksheet.module.database.dao.resolver.ResolvedRedirectDao
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

class ResolvedRedirectRepository(
    private val dao: ResolvedRedirectDao
) : ExportableRepository<ResolvedRedirect, ResolvedRedirectExportModel> {

    override val modelClass: KClass<ResolvedRedirectExportModel>
        get() = ResolvedRedirectExportModel::class

    fun getAll(): Flow<List<ResolvedRedirect>> {
        return dao.getAll()
    }

    suspend fun insert(inputUrl: String, resolvedUrl: String): ResolvedRedirect {
        val item = ResolvedRedirect(inputUrl, resolvedUrl)
        dao.insertReplace(item)
        return item
    }

    fun getForInputUrl(inputUrl: String): Pair<ResolvedRedirect, String?>? {
        val item = dao.getForInputUrl(inputUrl)
        return item?.let { it to it.resolvedUrl }
    }

    override suspend fun exportAll(): List<ResolvedRedirectExportModel> {
        return CommonImport.export(dao) { it.toExportModel() }
    }

    override suspend fun import(
        settings: ImportSettings,
        models: List<ResolvedRedirectExportModel>
    ): List<Pair<ResolvedRedirect, Long>> {
        return CommonImport.import(dao, settings, models) { it.fromExportModel() }
    }
}
