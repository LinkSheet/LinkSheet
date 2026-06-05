@file:OptIn(SensitivePreference::class)

package app.linksheet.feature.exportimport

import app.linksheet.api.BuildInfo
import app.linksheet.api.DeviceInfo
import app.linksheet.api.SensitivePreference
import fe.composekit.mozilla.components.support.base.log.logger.Logger
import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.annotations.TomlInlineTable
import com.akuleshov7.ktoml.file.TomlFileReader
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import fe.android.preference.helper.PreferenceRepository
import fe.gson.extension.json.`object`.asArray
import fe.gson.extension.json.`object`.asStringOrNull
import fe.linksheet.module.log.file.entry.SerializableLogEntry
import fe.linksheet.module.preference.app.AppPreferences
import fe.std.result.StdResult
import fe.std.result.isFailure
import fe.std.result.tryCatch
import fe.std.result.unaryPlus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okio.BufferedSource


class ExportImportUseCase(
    private val repository: PreferenceRepository,
    private val json: Json,
    private val toml: Toml,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val logger = Logger("ExportImportUseCase")

    enum class Format {
        Json, Toml
    }

    fun import(format: Format, source: BufferedSource): StdResult<Map<String, String>> {
        when (format) {
            Format.Json -> {
                val parseResult = tryCatch {
                    source.inputStream().reader().use { JsonParser.parseReader(it) }
                }
                if (parseResult.isFailure()) {
                    return +parseResult
                }

                val result = parseResult.value
                if (result == null) {
                    return +Exception("Failed to read file!")
                }

                val jsonElement = result
                val isLinkSheetPreferencesFile = jsonElement is JsonObject
                        && jsonElement.keySet().size == 1
                        && jsonElement.get("preferences") is JsonArray

                if (!isLinkSheetPreferencesFile) {
                    return +Exception("Provided file is not a LinkSheet preferences export!")
                }

                val preferences = jsonElement.asArray("preferences")
                val map = preferences.mapNotNull { preference ->
                    if (preference !is JsonObject) return@mapNotNull null

                    val name = preference.asStringOrNull("name")
                    val value = preference.asStringOrNull("value")

                    if (name == null || value == null) return@mapNotNull null
                    name to value
                }.toMap()

                return +map
            }

            Format.Toml -> {
                val dataResult = tryCatch {
                    TomlFileReader.decodeFromSource<ExportImportData>(source)
                }
                if (dataResult.isFailure()) {
                    return +dataResult
                }

                return +(dataResult.value.preferences ?: emptyMap())
            }
        }
    }

    fun export(includeLogHashKey: Boolean): Map<String, String> {
        val exclude = AppPreferences.sensitivePreferences.toMutableSet()
        if (includeLogHashKey) {
        }
        val preferences = AppPreferences.all.toMutableMap()
        exclude.forEach {
            preferences.remove(it.key)
        }

        @Suppress("UNCHECKED_CAST")
        val map = preferences.values
            .associate { it.key to repository.getAnyAsString(it) }
            .filterValues { it != null } as Map<String, String>
        return map
    }

    suspend fun exportToString(
        format: Format,
        includeLogHashKey: Boolean
    ): String = withContext(ioDispatcher) {
        val map = export(includeLogHashKey)
        when (format) {
            Format.Json -> {
                val fileContent = buildJsonObject {
                    put("preferences", buildPreferencesArray(map))
                }

                json.encodeToString(fileContent)
            }

            Format.Toml -> toml.encodeToString(ExportImportData(preferences = map))
        }
    }
}

private fun buildPreferencesArray(preferences: Map<String, String?>): kotlinx.serialization.json.JsonArray {
    return buildJsonArray {
        for ((key, value) in preferences) {
            addJsonObject {
                put("key", key)
                put("value", value)
            }
        }
    }
}

@Serializable
data class ExportImportData(
    val buildInfo: BuildInfo? = null,
    val deviceInfo: DeviceInfo? = null,
    val locale: String? = null,
    val fingerprint: String? = null,
    val activeExperiments: List<String>? = null,
    val preferences: Map<String, String>? = null,
    val log: List<@TomlInlineTable SerializableLogEntry>? = null
)
