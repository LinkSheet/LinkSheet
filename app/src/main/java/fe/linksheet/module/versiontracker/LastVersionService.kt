package fe.linksheet.module.versiontracker

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import fe.gson.dsl.jsonObject
import fe.gson.extension.json.array.elementsFilterNull
import fe.gson.extension.json.element.arrayOrNull
import fe.gson.extension.json.`object`.asIntOrNull
import fe.gson.extension.json.`object`.asStringOrNull
import fe.linksheet.module.systeminfo.BuildInfo

// TODO: Find a better way to do this
class LastVersionService(
    private val gson: Gson,
    private val buildInfo: BuildInfo,
) {
    private fun migrate(array: JsonArray): List<JsonObject> {
        fun maybeUpdate(it: JsonObject): JsonObject? {
            if (it.size() == 2 && it.has("v") && it.has("f")) {
                return it
            }

            val code = it.asIntOrNull("version_code") ?: return null
            val flavor = it.asStringOrNull("flavor") ?: return null
            return jsonObject {
                "v" += code
                "f" += flavor
            }
        }

        return array
            .elementsFilterNull<JsonObject>()
            .mapNotNull { maybeUpdate(it) }
            .filter { it.size() > 0 }
    }

    fun handleVersions(lastVersions: String?, migrate: Boolean = false): String? {
        val lastVersionArray = runCatching { parseLastVersions(lastVersions) }.getOrDefault(JsonArray())
        val lastVersionCodes = runCatching { parseVersionCodes(lastVersionArray) }.getOrDefault(emptySet())

        if (!lastVersionCodes.contains(buildInfo.versionCode)) {
            if (migrate) {
                val returnArrays = migrate(lastVersionArray).toMutableList()
                val currentObj = jsonObject {
                    "v" += buildInfo.versionCode
                    "f" += buildInfo.flavor
                }

                returnArrays.add(currentObj)
                return gson.toJson(returnArrays)
            }

            lastVersionArray.add(gson.toJsonTree(buildInfo))
            return gson.toJson(lastVersionArray)
        }

        return null
    }

    private fun parseLastVersions(lastVersions: String?): JsonArray {
        val versions = JsonParser.parseString(lastVersions).arrayOrNull()
        return versions ?: JsonArray()
    }

    private fun parseVersionCodes(versions: JsonArray?): Set<Int> {
        val lastVersionCodes = versions
            ?.elementsFilterNull<JsonObject>()
            ?.mapNotNull { it.asIntOrNull("version_code") ?: it.asIntOrNull("v") }
            ?.toSet()

        return lastVersionCodes ?: emptySet()
    }
}

//data class LastVersion(
//    @SerializedName("v") val versionCode: Int,
//    @SerializedName("f") val flavor: String,
//)
