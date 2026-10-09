package app.linksheet.feature.backup.impl.usecase

import app.linksheet.feature.backup.impl.core.BackupConfiguration
import app.linksheet.feature.backup.impl.core.BackupSchema
import app.linksheet.feature.backup.impl.core.BackupSchemaV1
import app.linksheet.feature.backup.impl.core.BackupType
import app.linksheet.feature.backup.impl.core.DatabaseType
import app.linksheet.feature.backup.impl.core.PreferenceRepositoryBackup
import app.linksheet.feature.backup.impl.core.PreferenceType
import app.linksheet.feature.backup.impl.ui.ExportSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackupUseCase internal constructor(
    private val configuration: BackupConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private fun exportPreferences(settings: Set<BackupType>): Map<PreferenceRepositoryBackup<*>, Map<String, String>> {
        fun getValues(holder: PreferenceRepositoryBackup<*>): Map<String, String> {
            @Suppress("UNCHECKED_CAST")
            return holder.preferences.values
                .filter { it.key !in holder.exclude }
                .associate { it.key to holder.repository.getAnyAsString(it) }
                .filterValues { it != null } as Map<String, String>
        }
        return configuration.preferenceBackups.filter { it.type in settings }.associateWith { getValues(it) }
    }

    suspend fun exportToString(settings: ExportSettings): String = withContext(ioDispatcher) {
        val set = settings.buildEnabledSet()
        val map = exportPreferences(set)
        val databaseItems = configuration.databaseBackups.filter { it.type in set }.flatMap { it.repository.exportAll() }

        val preferences = map.map { (holder, prefs) -> holder.type.name to prefs }.toMap()
        val data = BackupSchemaV1(preferences, databaseItems)

        configuration.json.encodeToString(BackupSchema.serializer(), data)
    }
}

private fun ExportSettings.buildEnabledSet(): Set<BackupType> {
    val set = mutableSetOf<BackupType>()
    if (includePreferences) set.add(PreferenceType.Preferences)
    if (includeExperiments) set.add(PreferenceType.Experiments)
    if (includeAppState) set.add(PreferenceType.AppState)
    if (includeSelectionHistory) set.add(DatabaseType.SelectionHistory)
    if (includeCache) set.add(DatabaseType.Cache)
    return set
}
