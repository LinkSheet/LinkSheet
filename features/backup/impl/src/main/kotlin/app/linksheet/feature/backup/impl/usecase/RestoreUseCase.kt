package app.linksheet.feature.backup.impl.usecase

import app.linksheet.feature.backup.api.ExportModel
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.api.RestoreMode
import app.linksheet.feature.backup.impl.core.BackupConfiguration
import app.linksheet.feature.backup.impl.core.BackupSchema
import app.linksheet.feature.backup.impl.core.BackupSchemaV1
import app.linksheet.feature.backup.impl.core.DatabaseBackup
import app.linksheet.feature.backup.impl.core.LegacyBackupSchema
import app.linksheet.feature.backup.impl.core.PreferenceRepositoryBackup
import app.linksheet.feature.backup.impl.core.PreferenceType
import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceEditor
import fe.android.preference.helper.PreferenceRepository
import fe.composekit.log.createLogger
import fe.composekit.preference.util.reload
import fe.std.result.StdResult
import fe.std.result.isFailure
import fe.std.result.isSuccess
import fe.std.result.tryCatch
import fe.std.result.unaryPlus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import okio.BufferedSource

@OptIn(ExperimentalSerializationApi::class)
class RestoreUseCase internal constructor(
    private val configuration: BackupConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val logger = createLogger<RestoreUseCase>()

    private fun findRepository(model: ExportModel): DatabaseBackup<*, *>? {
        return configuration.databaseBackups.firstOrNull { it.repository.canImport(model) }
    }

    suspend fun import(
        source: BufferedSource,
        settings: ImportSettings
    ): StdResult<RestoreResultWrapper> = withContext(ioDispatcher) {
        val result = tryCatch {
            source.use { configuration.json.decodeFromBufferedSource<BackupSchema>(it) }
        }
        if (result.isFailure()) {
            return@withContext +result
        }

        val entries = mutableListOf<RestoreEntry>()
        when (val data = result.value) {
            is LegacyBackupSchema -> {
                val holder = configuration.preferenceBackups.firstOrNull { it.type == PreferenceType.Preferences }
                if (holder != null) {
                    val prefs = data.preferences.associate { it.name to it.value }
                    entries.addAll(importPreferences(holder, prefs, settings))
                }
            }

            is BackupSchemaV1 -> {
                if (data.preferences != null) {
                    for ((name, prefs) in data.preferences) {
                        val holder = configuration.preferenceBackups.firstOrNull { it.type.name == name } ?: continue
                        entries.addAll(importPreferences(holder, prefs, settings))
                    }
                }

                if (data.databaseItems != null) {
                    val map = data.databaseItems.groupBy { findRepository(it) }
                    val modelsNotMatched = map[null]
                    if (modelsNotMatched != null) {
                        logger.error("Found ${modelsNotMatched.size} items without a matching repository")
                        for (model in modelsNotMatched) {
                            entries.add(
                                DatabaseRestoreEntry(
                                    model,
                                    RestoreResult.Failed(NoRepositoryException())
                                )
                            )
                        }
                    }

                    for ((holder, models) in map) {
                        if (holder == null) continue
                        val results = holder.repository.importModels(settings, models) ?: continue
                        for ((entity, model, id) in results) {
                            val entry = when (id) {
                                -1L -> {
                                    logger.debug("Couldn't insert '$model' as it already exists")
                                    DatabaseRestoreEntry(model, RestoreResult.Skipped)
                                }

                                else -> DatabaseRestoreEntry(model, RestoreResult.Restored)
                            }
                            entries.add(entry)
                        }
                    }
                }

            }
        }

        return@withContext +RestoreResultWrapper(entries)
    }

    private fun importPreferences(
        holder: PreferenceRepositoryBackup<*>,
        preferencesToImport: Map<String, String>,
        settings: ImportSettings
    ): List<RestoreEntry> {
        fun PreferenceEditor.Scope.updateOne(
            preference: Preference<*, *>,
            newValue: String
        ): PreferenceRestoreEntry {
            val hasStoredValue = holder.repository.hasStoredValue(preference)
            val canSet = settings.mode == RestoreMode.Replace ||
                    (settings.mode == RestoreMode.Merge && !hasStoredValue) ||
                    settings.mode == RestoreMode.EraseRestore
            if (!canSet) {
                logger.debug("Can't set '${preference.key}' as mode is '${settings.mode}' and has stored value is '${hasStoredValue}'")
                return PreferenceRestoreEntry(
                    holder.repository,
                    preference,
                    hasStoredValue,
                    RestoreResult.Skipped
                )
            }

            val result = tryCatch {
                setStringValueToPreference(preference, newValue)
            }
            if (result.isSuccess()) {
                return PreferenceRestoreEntry(
                    holder.repository,
                    preference,
                    hasStoredValue,
                    RestoreResult.Restored
                )
            }

            logger.error(
                "Failed to import preference '${preference.key}' into repository '${holder.type}'",
                result.exception
            )
            return PreferenceRestoreEntry(
                holder.repository,
                preference,
                hasStoredValue,
                RestoreResult.Failed(result.exception)
            )
        }

        val mappedPreferences = preferencesToImport.mapNotNull {
            val preference = holder.preferences[it.key] ?: return@mapNotNull null
            preference to it.value
        }

        val entries = mutableListOf<PreferenceRestoreEntry>()
        if (settings.mode == RestoreMode.EraseRestore) {
            holder.repository.edit { clearAll() }
        }

        holder.repository.edit {
            for ((preference, newValue) in mappedPreferences) {
                val result = updateOne(preference, newValue)
                entries.add(result)
            }
        }
        holder.migrate()
        for (entry in entries) {
            holder.repository.reload(entry.preference.key)
        }
        return entries
    }
}

data class RestoreResultWrapper(
    val entries: List<RestoreEntry>
)

interface RestoreEntry

data class DatabaseRestoreEntry(
    val model: ExportModel,
    val result: RestoreResult
) : RestoreEntry

data class PreferenceRestoreEntry(
    val repository: PreferenceRepository,
    val preference: Preference<*, *>,
    val hasStoredValue: Boolean,
    val result: RestoreResult
) : RestoreEntry

interface RestoreResult {
    data object Restored : RestoreResult
    class Failed(val exception: Throwable) : RestoreResult
    data object Skipped : RestoreResult
}

class NoRepositoryException : Exception()
