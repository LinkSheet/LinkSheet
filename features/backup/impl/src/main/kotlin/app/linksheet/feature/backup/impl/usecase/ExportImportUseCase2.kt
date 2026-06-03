@file:OptIn(SensitivePreference::class)

package app.linksheet.feature.backup.impl.usecase

//import fe.linksheet.module.preference.app.AppPreferences
//import fe.linksheet.module.preference.experiment.Experiments
//import fe.linksheet.module.preference.reload
//import fe.linksheet.module.preference.state.AppStatePreferences
import app.linksheet.api.SensitivePreference
import app.linksheet.feature.backup.api.ExportModel
import app.linksheet.feature.backup.api.ExportableRepository
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.impl.ui.exportimport.ExportSettings
import app.linksheet.log.createLogger
import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceDefinition
import fe.android.preference.helper.PreferenceRepository
import fe.composekit.preference.util.reload
import fe.std.result.isFailure
import fe.std.result.isSuccess
import fe.std.result.tryCatch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import okio.BufferedSource

sealed interface ExportType
sealed interface PreferenceType : ExportType {
    val name: String

    object Preferences : PreferenceType {
        override val name: String = "preferences"
    }
    object Experiments : PreferenceType {
        override val name: String = "experiments"
    }
    object AppState : PreferenceType {
        override val name: String = "appState"
    }
}

sealed interface DatabaseType : ExportType {
    data object SelectionHistory : DatabaseType
    data object Cache : DatabaseType
}

data class PreferenceExportImportHolder<T : PreferenceDefinition>(
    val type: PreferenceType,
    val repository: PreferenceRepository,
    val definition: T,
//    val preferences: Map<String, Preference<*, *>>,
    val exclude: Set<String> = emptySet()
) {
    val preferences: Map<String, Preference<*, *>>
        get() = definition.all

    fun migrate() {
        definition.runMigrations(repository)
    }
}

data class DatabaseExportImportHolder<T : ExportModel>(
    val type: ExportType,
    val repository: ExportableRepository<T>
)

@OptIn(ExperimentalSerializationApi::class)
class ExportImportUseCase2(
    private val holders: List<PreferenceExportImportHolder<*>>,
    private val databaseHolders: List<DatabaseExportImportHolder<*>>,
    private val json: Json,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val logger = createLogger<ExportImportUseCase2>()

    private fun findRepository(model: ExportModel): DatabaseExportImportHolder<*>? {
        return databaseHolders.firstOrNull { it.repository.canImport(model) }
    }

    suspend fun import(
        source: BufferedSource,
        settings: ImportSettings
    ) = withContext(ioDispatcher) {
        val result = tryCatch {
            source.use { json.decodeFromBufferedSource<ExportImportData2>(it) }
        }
        if (result.isFailure()) {
            return@withContext
        }

        val data = result.value
        if (data.preferences != null) {
            for ((name, prefs) in data.preferences) {
                val holder = holders.firstOrNull { it.type.name == name } ?: continue
                importPreferences(holder, prefs)
            }
        }

        if (data.databaseItems != null) {
            val map = data.databaseItems.groupBy { findRepository(it) }
            val modelsNotMatched = map[null]
            if (modelsNotMatched != null) {
                logger.error("Found ${modelsNotMatched.size} items without a matching repository")
            }

            for ((holder, items) in map) {
                holder?.repository?.importModels(settings, items)
            }
        }
    }

    private fun importPreferences(
        holder: PreferenceExportImportHolder<*>,
        preferencesToImport: Map<String, String>
    ) {
        val mappedPreferences = preferencesToImport.mapNotNull {
            val preference = holder.preferences[it.key] ?: return@mapNotNull null
            preference to it.value
        }

        val imported = mutableListOf<Preference<*, *>>()
        holder.repository.edit {
            for ((preference, newValue) in mappedPreferences) {
//                holder.repository.hasStoredValue()
                val result = tryCatch {
                    setStringValueToPreference(preference, newValue)
                }
                if (result.isSuccess()) {
                    imported.add(preference)
                } else {
                    logger.error(
                        "Failed to import preference '${preference.key}' into repository '${holder.type}'",
                        result.exception
                    )
                }
            }
        }
        holder.migrate()
        for (preference in imported) {
            holder.repository.reload(preference.key)
        }
    }

    private fun exportPreferences(settings: Set<ExportType>): Map<PreferenceExportImportHolder<*>, Map<String, String>> {
        fun getValues(holder: PreferenceExportImportHolder<*>): Map<String, String> {
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
