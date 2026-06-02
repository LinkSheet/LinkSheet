package fe.linksheet.module.repository.resolver

import app.linksheet.feature.backup.api.ExportableRepository
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.model.DisableInAppBrowserInSelectedExportModel
import app.linksheet.feature.backup.model.ResolvedRedirectExportModel
import app.linksheet.feature.backup.model.fromExportModel
import app.linksheet.feature.backup.model.toExportModel
import fe.linksheet.module.database.dao.resolver.ResolvedRedirectDao
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.reflect.KClass

class ResolvedRedirectRepository(
    private val dao: ResolvedRedirectDao
) : ExportableRepository<ResolvedRedirectExportModel> {

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
        return dao.getAll().first().map { it.toExportModel() }
    }

    override suspend fun import(
        settings: ImportSettings,
        models: List<ResolvedRedirectExportModel>
    ) {
        val entities = models.map { it.fromExportModel() }
        if (settings.replace) {
            dao.insertReplace(entities)
        } else {
            dao.insert(entities)
        }
    }
}
