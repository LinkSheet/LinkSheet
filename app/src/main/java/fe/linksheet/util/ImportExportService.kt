@file:OptIn(ExperimentalTime::class)

package fe.linksheet.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import fe.gson.extension.json.`object`.asArray
import fe.gson.extension.json.`object`.asStringOrNull
import fe.linksheet.R
import fe.linksheet.extension.android.bufferedReader
import fe.linksheet.extension.android.bufferedWriter
import fe.linksheet.module.clock.ClockProvider
import fe.linksheet.util.intent.buildIntent
import fe.std.result.StdResult
import fe.std.result.isFailure
import fe.std.result.tryCatch
import fe.std.result.unaryPlus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

class ImportExportService(val context: Context, val clockProvider: ClockProvider) {
    companion object {
        val ImportIntent = buildIntent(Intent.ACTION_OPEN_DOCUMENT) {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }

        private val exportIntent = buildIntent(Intent.ACTION_CREATE_DOCUMENT) {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }

        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm")
    }

    fun createExportIntent(now: Instant = clockProvider.now()): Intent {
        val zonedDateTime = now.toJavaInstant().atZone(clockProvider.zoneId)
        val nowString = zonedDateTime.format(dateTimeFormatter)
        return Intent(exportIntent)
            .putExtra(
                Intent.EXTRA_TITLE,
                context.getString(R.string.export_file_name, nowString)
            )
    }

    @Throws(Exception::class)
    private fun openDescriptor(uri: Uri, mode: String): ParcelFileDescriptor? {
        return context.contentResolver.openFileDescriptor(uri, mode)
    }

    suspend fun exportPreferencesToUri(uri: Uri, preferenceJson: String) = withContext(Dispatchers.IO) {
        tryCatch {
            openDescriptor(uri, "w")
                ?.bufferedWriter()
                ?.use { it.write(preferenceJson) }
                ?: Unit
        }
    }

    suspend fun importPreferencesFromUri(uri: Uri): StdResult<Map<String, String>> = withContext(Dispatchers.IO) {
        val parseResult = tryCatch {
            openDescriptor(uri, "r")
                ?.bufferedReader()
                ?.use { JsonParser.parseReader(it) }
        }

        if (parseResult.isFailure()) {
            return@withContext +parseResult
        }

        val result = parseResult.value
        if (result == null) {
            return@withContext +Exception("Failed to read file!")
        }

        val jsonElement = result
        val isLinkSheetPreferencesFile = jsonElement is JsonObject
                && jsonElement.keySet().size == 1
                && jsonElement.get("preferences") is JsonArray

        if (!isLinkSheetPreferencesFile) {
            return@withContext +Exception("Provided file is not a LinkSheet preferences export!")
        }

        val preferences = jsonElement.asArray("preferences")
        val map = preferences.mapNotNull { preference ->
            if (preference !is JsonObject) return@mapNotNull null

            val name = preference.asStringOrNull("name")
            val value = preference.asStringOrNull("value")

            if (name == null || value == null) return@mapNotNull null
            name to value
        }.toMap()

        +map
    }
}
