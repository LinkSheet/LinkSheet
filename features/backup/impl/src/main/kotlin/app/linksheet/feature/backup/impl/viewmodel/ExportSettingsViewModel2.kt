package app.linksheet.feature.backup.impl.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.impl.R
import app.linksheet.feature.backup.impl.usecase.ExportImportUseCase2
import fe.composekit.intent.buildIntent
import fe.linksheet.extension.android.bufferedSource
import fe.linksheet.extension.android.bufferedWriter
import fe.linksheet.extension.android.useDescriptor
import fe.std.result.StdResult
import fe.std.result.isFailure
import fe.std.result.tryCatch
import fe.std.result.unaryPlus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class ExportSettingsViewModel2(
    val context: Application,
    private val clock: Clock,
    private val useCase: ExportImportUseCase2
) : ViewModel() {
//    private val importExportService = ExportImportService(context, clock)
    companion object {
        private val formatter = LocalDateTime.Format {
            byUnicodePattern("uuuu-MM-dd'T'HH:mm[:ss]")
        }
    }

    fun createImportIntent(): Intent {
        return buildIntent(Intent.ACTION_OPEN_DOCUMENT) {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
    }

    fun createExportIntent(): Intent {
        val now = clock.now()
        val nowString = now.toLocalDateTime(TimeZone.currentSystemDefault()).format(formatter)
        val fileName = context.getString(R.string.export_file_name, nowString)
        val extension = ".json"
        return buildIntent(Intent.ACTION_CREATE_DOCUMENT) {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(
                Intent.EXTRA_TITLE,
                fileName + extension
            )
        }
    }

    suspend fun importPreferences(uri: Uri): StdResult<out Any> = withContext(Dispatchers.IO) {
        val settings = ImportSettings.Default
        val result = tryCatch {
            context.useDescriptor(uri, "r") { pfd ->
                pfd.bufferedSource().use {
                    useCase.import(it, settings)
                }
            }
        }
        if (result.isFailure()) {
            return@withContext +result
        }

        val value = result.value
        return@withContext +result
//        val mappedPreferences = useCase.importPreferences(value)
//        return +preferenceRepository.refreshPostImport(mappedPreferences)
    }

    suspend fun exportPreferences(
        uri: Uri,
        includeLogHashKey: Boolean
    ): StdResult<Unit> = withContext(Dispatchers.IO) {
        val preferences = useCase.exportToString(includeLogHashKey)
        tryCatch {
            context.useDescriptor(uri, "w") { pfd ->
                pfd.bufferedWriter().use { it.write(preferences) }
            }
        }
        +Unit
    }
}
