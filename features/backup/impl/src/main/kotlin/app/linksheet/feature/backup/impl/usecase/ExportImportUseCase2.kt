@file:OptIn(SensitivePreference::class)

package app.linksheet.feature.backup.impl.usecase

import app.linksheet.api.SensitivePreference
import app.linksheet.feature.backup.api.ExportModel
import app.linksheet.feature.backup.api.ExportableRepository
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.log.createLogger
import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceDefinition
import fe.android.preference.helper.PreferenceRepository
//import fe.linksheet.module.preference.app.AppPreferences
//import fe.linksheet.module.preference.experiment.Experiments
//import fe.linksheet.module.preference.reload
//import fe.linksheet.module.preference.state.AppStatePreferences
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

data class PreferenceExportImportHolder<Definition : PreferenceDefinition>(
    val name: String,
    val repository: PreferenceRepository,
    val definition: Definition,
//    val preferences: Map<String, Preference<*, *>>,
    val exclude: Set<String> = emptySet()
) {
    val preferences: Map<String, Preference<*, *>>
        get() = definition.all

    fun migrate() {
        definition.runMigrations(repository)
    }
}

@OptIn(ExperimentalSerializationApi::class)
class ExportImportUseCase2(
    private val holders: List<PreferenceExportImportHolder<*>>,
    private val exportableRepositories: List<ExportableRepository<*>>,
    private val json: Json,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val logger = createLogger<ExportImportUseCase2>()
//    private val preferencesHolder = PreferenceExportImportHolder(
//        name = "preferences",
//        repository = preferenceRepository,
//        definition = AppPreferences,
//        exclude = AppPreferences.sensitivePreferences.mapToSet { it.key }
//    )
//    private val experimentsHolder = PreferenceExportImportHolder(
//        name = "experiments",
//        repository = experimentRepository,
//        definition = Experiments,
//    )
//    private val appStateHolder = PreferenceExportImportHolder(
//        name = "appState",
//        repository = appStateRepository,
//        definition = AppStatePreferences,
//    )
//    private val preferenceRepositories = listOf(
//        preferencesHolder,
//        experimentsHolder,
//        appStateHolder
//    )
    private fun findRepository(model: ExportModel): ExportableRepository<out ExportModel>? {
        return exportableRepositories.firstOrNull { it.canImport(model) }
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
                val holder = holders.firstOrNull { it.name == name } ?: continue
                importPreferences(holder, prefs)
            }
        }

        if (data.databaseItems != null) {
            val map = data.databaseItems.groupBy { findRepository(it) }
            val modelsNotMatched = map[null]
            if (modelsNotMatched != null) {
                logger.error("Found ${modelsNotMatched.size} items without a matching repository")
            }

            for ((repository, items) in map) {
                repository?.importModels(settings, items)
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
                val result = tryCatch {
                    holder.repository.setStringValueToPreference(preference, newValue)
                }
                if (result.isSuccess()) {
                    imported.add(preference)
                } else {
                    logger.error(
                        "Failed to import preference '${preference.key}' into repository '${holder.name}'",
                        result.exception
                    )
                }
            }
        }
        holder.migrate()
//        for (preference in imported) {
//            holder.repository.reload(preference.key)
//        }
    }

    private fun exportPreferences(): Map<PreferenceExportImportHolder<*>, Map<String, String>> {
        fun getValues(holder: PreferenceExportImportHolder<*>): Map<String, String> {
            return holder.preferences.values
                .filter { it.key !in holder.exclude }
                .associate { it.key to holder.repository.getAnyAsString(it) }
                .filterValues { it != null } as Map<String, String>
        }

        return holders.associateWith { getValues(it) }
    }

    suspend fun exportToString(includeLogHashKey: Boolean): String = withContext(ioDispatcher) {
        val map = exportPreferences()
        val databaseItems = exportableRepositories.flatMap { it.exportAll() }

        val preferences = map.map {  (holder, prefs) -> holder.name to prefs }.toMap()
        val data = ExportImportData2(preferences, databaseItems)

        json.encodeToString(data)
    }
}

@Serializable
data class ExportImportData2(
    val preferences: Map<String, Map<String, String>>? = null,
    val databaseItems: List<ExportModel>? = null
)
