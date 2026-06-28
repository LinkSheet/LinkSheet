package app.linksheet.feature.backup.api

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlin.reflect.KClass

interface ExportableRepository<Entity, Model : ExportModel> {
    val modelClass: KClass<Model>
    suspend fun exportAll(): List<Model>
    suspend fun eraseAll()
    suspend fun import(settings: ImportSettings, models: List<Model>): List<Pair<Entity, Long>>

    suspend fun importModels(
        settings: ImportSettings,
        models: List<ExportModel>
    ): List<ImportResult<Entity, Model>>? {
        val items = models.mapNotNull {
            @Suppress("UNCHECKED_CAST")
            it as? Model
        }
        if (settings.mode == RestoreMode.EraseRestore) {
            eraseAll()
        }
        val inserts = import(settings, items)
        if (items.size != inserts.size) {
            return null
        }

        return items.indices.map { i ->
            val (entity, id) = inserts[i]
            ImportResult(entity, items[i], id)
        }
    }

    fun canImport(model: ExportModel): Boolean {
        return modelClass.java.isAssignableFrom(model::class.java)
    }
}

data class ImportResult<Entity, Model : ExportModel>(
    val entity: Entity,
    val model: Model,
    val id: Long,
)

@Parcelize
data class ImportSettings(
    val mode: RestoreMode
) : Parcelable {
    companion object {
        val Default = ImportSettings(RestoreMode.Merge)
    }

    @IgnoredOnParcel
    val replace = (mode == RestoreMode.EraseRestore || mode == RestoreMode.Replace)
}

enum class RestoreMode {
    EraseRestore, Replace, Merge
}
