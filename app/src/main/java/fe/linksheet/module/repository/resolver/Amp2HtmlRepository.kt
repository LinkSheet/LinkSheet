package fe.linksheet.module.repository.resolver

import app.linksheet.feature.backup.api.CommonImport
import app.linksheet.feature.backup.api.ExportableRepository
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.model.Amp2HtmlMappingExportModel
import app.linksheet.feature.backup.model.fromExportModel
import app.linksheet.feature.backup.model.toExportModel
import fe.linksheet.module.database.dao.resolver.Amp2HtmlMappingDao
import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

class Amp2HtmlRepository(
    private val dao: Amp2HtmlMappingDao,
) : ExportableRepository<Amp2HtmlMapping, Amp2HtmlMappingExportModel> {

    override val modelClass: KClass<Amp2HtmlMappingExportModel>
        get() = Amp2HtmlMappingExportModel::class

    fun getAll(): Flow<List<Amp2HtmlMapping>> {
        return dao.getAll()
    }

    suspend fun insert(inputUrl: String, resolvedUrl: String): Amp2HtmlMapping {
        val item = Amp2HtmlMapping(inputUrl, resolvedUrl)
        dao.insertReplace(item)
        return item
    }

    fun getForInputUrl(inputUrl: String): Pair<Amp2HtmlMapping, String?>? {
        val item = dao.getForInputUrl(inputUrl)
        return item?.let { it to it.canonicalUrl }
    }

    override suspend fun exportAll(): List<Amp2HtmlMappingExportModel> {
        return CommonImport.export(dao) { it.toExportModel() }
    }

    override suspend fun eraseAll() {
        dao.deleteAll()
    }

    override suspend fun import(settings: ImportSettings, models: List<Amp2HtmlMappingExportModel>): List<Pair<Amp2HtmlMapping, Long>> {
        return CommonImport.import(dao, settings, models) { it.fromExportModel() }
    }
}
