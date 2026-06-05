package app.linksheet.feature.backup.impl.usecase

import app.linksheet.feature.backup.api.ExportModel
import app.linksheet.feature.backup.impl.core.DatabaseExportImportHolder
import app.linksheet.feature.backup.impl.core.DatabaseType
import app.linksheet.feature.backup.impl.core.ExportType
import app.linksheet.feature.backup.impl.core.PreferenceExportImportHolder
import app.linksheet.feature.backup.impl.core.PreferenceType
import app.linksheet.feature.backup.impl.ui.exportimport.ExportSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class BackupUseCase(
    private val holders: List<PreferenceExportImportHolder<*>>,
    private val databaseHolders: List<DatabaseExportImportHolder<*, *>>,
    private val json: Json,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private fun exportPreferences(settings: Set<ExportType>): Map<PreferenceExportImportHolder<*>, Map<String, String>> {
        fun getValues(holder: PreferenceExportImportHolder<*>): Map<String, String> {
            @Suppress("UNCHECKED_CAST")
            return holder.preferences.values
                .filter { it.key !in holder.exclude }
                .associate { it.key to holder.repository.getAnyAsString(it) }
                .filterValues { it != null } as Map<String, String>
        }
        return holders.filter { it.type in settings }.associateWith { getValues(it) }
    }

    suspend fun exportToString(settings: ExportSettings): String = withContext(ioDispatcher) {
        val set = settings.buildEnabledSet()
        val map = exportPreferences(set)
        val databaseItems = databaseHolders.filter { it.type in set }.flatMap { it.repository.exportAll() }

        val preferences = map.map { (holder, prefs) -> holder.type.name to prefs }.toMap()
        val data = ExportImportData2(preferences, databaseItems)

        json.encodeToString(data)
    }
}

private fun ExportSettings.buildEnabledSet(): Set<ExportType> {
    val set = mutableSetOf<ExportType>()
    if (includePreferences) set.add(PreferenceType.Preferences)
    if (includeExperiments) set.add(PreferenceType.Experiments)
    if (includeAppState) set.add(PreferenceType.AppState)
    if (includeSelectionHistory) set.add(DatabaseType.SelectionHistory)
    if (includeCache) set.add(DatabaseType.Cache)
    return set
}

@Serializable
data class ExportImportData2(
    val preferences: Map<String, Map<String, String>>? = null,
    val databaseItems: List<ExportModel>? = null
)
