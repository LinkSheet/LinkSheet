package app.linksheet.feature.backup.impl.ui

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import app.linksheet.feature.backup.impl.R
import app.linksheet.feature.backup.impl.ui.exportimport.FileSelectionResult
import app.linksheet.feature.backup.impl.ui.exportimport.rememberExportSettingsDialog
import app.linksheet.feature.backup.impl.ui.exportimport.rememberImportSettingsDialog
import app.linksheet.feature.backup.impl.viewmodel.ExportSettingsViewModel2
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.std.result.isSuccess
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ExportImportSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: ExportSettingsViewModel2 = koinViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val exportDialog = rememberExportSettingsDialog(
        exportIntent = viewModel.createExportIntent(),
        onResult = { (result, includeLogHashKey) ->
            scope.launch {
                when (result) {
                    is FileSelectionResult.Ok -> {
                        val result = viewModel.exportPreferences(result.uri, includeLogHashKey)
                        val resId = if (result.isSuccess()) R.string.export_settings__text_success
                        else R.string.export_settings__text_failure

                        Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
                    }

                    FileSelectionResult.Cancelled -> {}
                }
            }
        }
    )

    val importDialog = rememberImportSettingsDialog(
        importIntent = viewModel.createImportIntent(),
        onResult = { result ->
            scope.launch {
                when (result) {
                    is FileSelectionResult.Ok -> {
                        val result = viewModel.importPreferences(result.uri)
                        val resId = if (result.isSuccess()) R.string.import_settings__text_success
                        else R.string.import_settings__text_failure

                        Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
                    }

                    FileSelectionResult.Cancelled -> {}
                }
            }
        }
    )

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.export_import_settings),
        onBackPressed = onBackPressed
    ) {
        group(size = 2) {
            item(key = R.string.export) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.export),
                    supportingContent = textContent(R.string.export_explainer),
                    icon = Icons.Outlined.ImportExport.iconPainter,
                    onClick = {
                        exportDialog.open()
                    }
                )
            }

            item(key = R.string.import_headline) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.import_headline),
                    supportingContent = textContent(R.string.import_explainer),
                    icon = Icons.Outlined.ImportExport.iconPainter,
                    onClick = {
                        importDialog.open()
                    }
                )
            }
        }
    }
}

