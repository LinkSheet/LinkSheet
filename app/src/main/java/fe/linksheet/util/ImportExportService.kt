@file:OptIn(ExperimentalTime::class)

package fe.linksheet.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.ParcelFileDescriptor
import fe.composekit.intent.buildIntent
import fe.linksheet.R
import fe.linksheet.extension.android.bufferedWriter
import fe.std.result.tryCatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

class ImportExportService(val context: Context, val clock: Clock, val zoneId: ZoneId) {
    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm")
    }

    fun createImportIntent(format: ExportImportUseCase.Format): Intent {
        return buildIntent(Intent.ACTION_OPEN_DOCUMENT) {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = format.mimeType
        }
    }

    private val ExportImportUseCase.Format.mimeType: String
        get() = when(this) {
            ExportImportUseCase.Format.Json -> "application/json"
            ExportImportUseCase.Format.Toml -> "application/toml"
        }

    fun createExportIntent(format: ExportImportUseCase.Format, now: Instant = clock.now()): Intent {
        val nowString = now.toJavaInstant().atZone(zoneId).format(dateTimeFormatter)
        val fileName = context.getString(R.string.export_file_name, nowString)
        val extension = when(format){
            ExportImportUseCase.Format.Json -> ".json"
            ExportImportUseCase.Format.Toml -> ".toml"
        }

        return buildIntent(Intent.ACTION_CREATE_DOCUMENT) {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = format.mimeType
            putExtra(
                Intent.EXTRA_TITLE,
                fileName + extension
            )
        }
    }

    @Throws(Exception::class)
    fun openDescriptor(uri: Uri, mode: String): ParcelFileDescriptor? {
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

//    suspend fun importPreferencesFromUri(uri: Uri): StdResult<Map<String, String>> = withContext(Dispatchers.IO) {
//        val parseResult = tryCatch {
//            openDescriptor(uri, "r")
//                ?.bufferedReader()
//                ?.use { JsonParser.parseReader(it) }
//        }
//    }
}
