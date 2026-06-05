package app.linksheet.feature.backup.api

import app.linksheet.api.database.BaseDao
import kotlinx.coroutines.flow.first

object CommonImport {

    suspend fun <Entity, Model> export(
        dao: BaseDao<Entity>,
        toModel: (Entity) -> Model
    ): List<Model> {
        return dao.getAll().first().map(toModel)
    }

    suspend fun <Entity, Model : ExportModel> import(
        dao: BaseDao<Entity>,
        settings: ImportSettings,
        models: List<Model>,
        fromModel: (Model) -> Entity
    ): List<Pair<Entity, Long>> {
        val entities = models.map(fromModel)
        val ids = when {
            settings.replace -> dao.insertReplace(entities)
            else -> dao.insert(entities)
        }

        return entities.zip(ids)
    }
}
