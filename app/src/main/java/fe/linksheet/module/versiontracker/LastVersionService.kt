package fe.linksheet.module.versiontracker

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import fe.gson.extension.json.array.elementsOrNull
import fe.gson.extension.json.element.arrayOrNull
import fe.gson.extension.json.`object`.asIntOrNull
import fe.linksheet.util.BuildInfo

// TODO: Find a better way to do this
class LastVersionService(
    private val gson: Gson,
    private val buildInfo: BuildInfo,
) {
    fun handleVersions(lastVersions: String?): String? {
        val lastVersionArray = runCatching { parseLastVersions(lastVersions) }.getOrDefault(JsonArray())
        val lastVersionCodes = runCatching { parseVersionCodes(lastVersionArray) }.getOrDefault(emptySet())

        if (!lastVersionCodes.contains(buildInfo.versionCode)) {
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
            ?.elementsOrNull<JsonObject>(keepNulls = false)
            ?.mapNotNull { it?.asIntOrNull("version_code") }
            ?.toSet()

        return lastVersionCodes ?: emptySet()
    }
}
