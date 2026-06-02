package app.linksheet.feature.backup.api

import kotlin.reflect.KClass

interface ExportableRepository<T : ExportModel> {
    val modelClass: KClass<T>
    suspend fun exportAll(): List<T>
    suspend fun import(settings: ImportSettings, models: List<T>)

    suspend fun importModels(settings: ImportSettings, models: List<ExportModel>) {
        val items = models.mapNotNull {
            @Suppress("UNCHECKED_CAST")
            it as? T
        }
        return import(settings, items)
    }

    fun canImport(model: ExportModel): Boolean {
        return model::class.isInstance(modelClass)
    }
}

data class ImportSettings(
    val replace: Boolean
) {
    companion object{
        val Default = ImportSettings(true)
    }
}
