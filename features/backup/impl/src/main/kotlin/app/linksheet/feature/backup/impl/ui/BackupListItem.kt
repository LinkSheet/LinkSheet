package app.linksheet.feature.backup.impl.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.impl.R
import app.linksheet.feature.backup.impl.ui.exportimport.ExportResult
import app.linksheet.feature.backup.impl.ui.exportimport.ExportSettings
import app.linksheet.feature.backup.impl.ui.exportimport.FileSelectionResult
import app.linksheet.feature.backup.impl.ui.exportimport.ImportResult
import app.linksheet.feature.backup.impl.ui.exportimport.rememberExportSettingsDialog2
import app.linksheet.feature.backup.impl.ui.exportimport.rememberImportSettingsDialog
import app.linksheet.feature.backup.impl.usecase.RestoreResultWrapper
import fe.android.compose.dialog.helper.input.InputResultDialogState
import fe.android.compose.dialog.helper.result.ResultDialogState
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.composekit.layout.column.SaneLazyColumnGroupScope
import fe.std.result.StdResult
import fe.std.result.isSuccess
import kotlinx.coroutines.launch

@Composable
fun backupDialog(
    exportPreferences: suspend (Uri, ExportSettings) -> StdResult<Unit>
): InputResultDialogState<Intent, ExportResult> {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val state = rememberExportSettingsDialog2(
        onResult = { exportResult ->
            scope.launch {
                when (val result = exportResult.result) {
                    is FileSelectionResult.Ok -> {
                        val result = exportPreferences(result.uri, exportResult.settings)
                        val resId = if (result.isSuccess()) R.string.export_settings__text_success
                        else R.string.export_settings__text_failure

                        Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
                    }
                    FileSelectionResult.Cancelled -> {}
                }
            }
        }
    )
    return state
}

@Composable
fun restoreDialog(
    importIntent: Intent,
    importPreferences: suspend (Uri, ImportSettings) -> StdResult<RestoreResultWrapper?>
): ResultDialogState<ImportResult> {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val importDialog = rememberImportSettingsDialog(
        importIntent = importIntent,
        onResult = { importResult ->
            scope.launch {
                when (val result = importResult.result) {
                    is FileSelectionResult.Ok -> {
                        val result = importPreferences(result.uri, importResult.settings)
                        val resId = if (result.isSuccess()) R.string.import_settings__text_success
                        else R.string.import_settings__text_failure

                        Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
                    }

                    FileSelectionResult.Cancelled -> {}
                }
            }
        }
    )
    return importDialog
}

fun SaneLazyColumnGroupScope.backupListItem(open: () -> Unit) {
    item(key = R.string.settings_backup__title_backup) { padding, shape ->
        DefaultTwoLineIconClickableShapeListItem(
            shape = shape,
            padding = padding,
            headlineContent = textContent(R.string.settings_backup__title_backup),
            supportingContent = textContent(R.string.settings_backup__subtitle_backup),
            icon = Icons.Rounded.Backup.iconPainter,
            onClick = open
        )
    }
}

fun SaneLazyColumnGroupScope.restoreListItem(open: () -> Unit) {
    item(key = R.string.settings_backup__title_restore) { padding, shape ->
        DefaultTwoLineIconClickableShapeListItem(
            shape = shape,
            padding = padding,
            headlineContent = textContent(R.string.settings_backup__title_restore),
            supportingContent = textContent(R.string.settings_backup__subtitle_restore),
            icon = Icons.Rounded.SettingsBackupRestore.iconPainter,
            onClick = open
        )
    }
}
