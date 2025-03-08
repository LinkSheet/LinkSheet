package fe.linksheet.util

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import fe.gson.extension.json.`object`.asArray
import fe.gson.extension.json.`object`.asStringOrNull
import fe.linksheet.extension.android.bufferedReader
import fe.linksheet.extension.android.bufferedWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImportExportService(val context: Context) {
    @Throws(Exception::class)
    private fun openDescriptor(uri: Uri, mode: String): ParcelFileDescriptor? {
        return context.contentResolver.openFileDescriptor(uri, mode)
    }

    suspend fun exportPreferencesToUri(uri: Uri, preferenceJson: String) = withContext(Dispatchers.IO) {
        openDescriptor(uri, "w")?.use { descriptor -> descriptor.bufferedWriter().write(preferenceJson) }
    }

    suspend fun importPreferencesFromUri(uri: Uri): Result<Map<String, String>> = withContext(Dispatchers.IO) {
        val parseResult = runCatching {
            openDescriptor(uri, "r")?.use { descriptor ->
                JsonParser.parseReader(descriptor.bufferedReader())
            }
        }

        if (parseResult.isFailure) {
            return@withContext Result.failure(parseResult.exceptionOrNull()!!)
        }

        val result = parseResult.getOrNull()
        if (result == null) {
            return@withContext Result.failure(Exception("Failed to read file!"))
        }

        val jsonElement = result
        val isLinkSheetPreferencesFile = jsonElement is JsonObject
                && jsonElement.keySet().size == 1
                && jsonElement.get("preferences") is JsonArray

        if (!isLinkSheetPreferencesFile) {
            return@withContext Result.failure(Exception("Provided file is not a LinkSheet preferences export!"))
        }

        val preferences = jsonElement.asArray("preferences")
        val map = preferences.mapNotNull { preference ->
            if (preference !is JsonObject) return@mapNotNull null

            val name = preference.asStringOrNull("name")
            val value = preference.asStringOrNull("value")

            if (name == null || value == null) return@mapNotNull null
            name to value
        }.toMap()

        Result.success(map)
    }
}
