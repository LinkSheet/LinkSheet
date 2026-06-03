package app.linksheet.feature.backup.api

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
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

@Parcelize
data class ImportSettings(
    val mode: RestoreMode
) : Parcelable {
    companion object {
        val Default = ImportSettings(RestoreMode.Merge)
    }

    @IgnoredOnParcel
    @Deprecated("Remove this")
    val replace = mode == RestoreMode.Replace
}

enum class RestoreMode {
    EraseRestore, Replace, Merge
}
