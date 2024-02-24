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

class ImportExportService(val context: Context) {
    @Throws(Exception::class)
    private fun openDescriptor(uri: Uri, mode: String): ParcelFileDescriptor {
        return context.contentResolver.openFileDescriptor(uri, mode) ?: throw Exception("Failed to open file!")
    }

    fun exportPreferencesToUri(uri: Uri, preferenceJson: String): Result<Unit> {
        return runCatching {
            openDescriptor(uri, "w").bufferedWriter().use { fos -> fos.write(preferenceJson) }
        }
    }

    fun importPreferencesFromUri(uri: Uri): Result<Map<String, String>> {
        val parseResult = runCatching {
            openDescriptor(uri, "r").bufferedReader().use { JsonParser.parseReader(it) }
        }


        if (parseResult.isFailure) {
            return Result.failure(parseResult.exceptionOrNull()!!)
        }

        val jsonElement = parseResult.getOrNull()!!
        val isLinkSheetPreferencesFile = jsonElement is JsonObject
                && jsonElement.keySet().size == 1
                && jsonElement.get("preferences") is JsonArray

        if (!isLinkSheetPreferencesFile) {
            return Result.failure(Exception("Provided file is not a LinkSheet preferences export!"))
        }

        val preferences = (jsonElement as JsonObject).asArray("preferences")
        val map = preferences.mapNotNull { preference ->
            if (preference !is JsonObject) return@mapNotNull null

            val name = preference.asStringOrNull("name")
            val value = preference.asStringOrNull("value")

            if (name == null || value == null) return@mapNotNull null
            name to value
        }.toMap()

        return Result.success(map)
    }
}
