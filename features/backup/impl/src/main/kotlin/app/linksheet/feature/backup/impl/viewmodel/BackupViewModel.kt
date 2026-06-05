package app.linksheet.feature.backup.impl.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.impl.R
import app.linksheet.feature.backup.impl.ui.exportimport.ExportSettings
import app.linksheet.feature.backup.impl.usecase.BackupUseCase
import app.linksheet.feature.backup.impl.usecase.RestoreResultWrapper
import app.linksheet.feature.backup.impl.usecase.RestoreUseCase
import fe.composekit.intent.buildIntent
import fe.linksheet.extension.android.bufferedSource
import fe.linksheet.extension.android.bufferedWriter
import fe.linksheet.extension.android.useFileDescriptor
import fe.std.result.StdResult
import fe.std.result.tryCatch
import fe.std.result.unaryPlus
import fe.std.result.unwrap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class BackupViewModel(
    val context: Application,
    private val clock: Clock,
    private val backupUseCase: BackupUseCase,
    private val restoreUseCase: RestoreUseCase
) : ViewModel() {

    companion object {
        private val formatter = LocalDateTime.Format {
            byUnicodePattern("uuuu-MM-dd'T'HH:mm[:ss]")
        }
        val ImportIntent = buildIntent(Intent.ACTION_OPEN_DOCUMENT) {
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

    suspend fun importPreferences(
        uri: Uri,
        settings: ImportSettings
    ): StdResult<RestoreResultWrapper?> = withContext(Dispatchers.IO) {
        val result = tryCatch {
            context.useFileDescriptor(uri, "r") { pfd ->
                pfd.bufferedSource().use {
                    restoreUseCase.import(it, settings)
                }
            }?.unwrap()
        }
//        if (result.isFailure()) {
//            return@withContext +result
//        }

        return@withContext result
    }

    suspend fun exportPreferences(uri: Uri, settings: ExportSettings): StdResult<Unit> = withContext(Dispatchers.IO) {
        val preferences = backupUseCase.exportToString(settings)
        tryCatch {
            context.useFileDescriptor(uri, "w") { pfd ->
                pfd.bufferedWriter().use { it.write(preferences) }
            }
        }
        +Unit
    }
}
