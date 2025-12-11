package fe.linksheet.composable.page.settings.advanced.exportimport

import android.content.Intent
import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.linksheet.compose.theme.DialogTitleStyle
import fe.android.compose.dialog.helper.result.ResultDialog
import fe.android.compose.dialog.helper.result.ResultDialogState
import fe.android.compose.dialog.helper.result.rememberResultDialogState
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.span.helper.composable.createAnnotatedString
import fe.composekit.component.ContentType
import fe.composekit.component.dialog.SaneAlertDialogTextButton
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.CheckboxListItem
import fe.linksheet.R
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ExportResult(
    val result: @RawValue FileSelectionResult,
    val includeLogHashKey: Boolean,
) : Parcelable

@Composable
fun rememberExportSettingsDialog(
    exportIntent: Intent,
    onResult: (ExportResult) -> Unit
): ResultDialogState<ExportResult> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberResultDialogState<ExportResult>()

    ResultDialog(state = state, onClose = onResult) {
        ExportSettingsDialog(
            exportIntent = exportIntent,
            onDismiss = interaction.wrap(FeedbackType.Decline, state::dismiss),
            onResult = interaction.wrap(FeedbackType.Confirm, state::close),
        )
    }

    return state
}


@Composable
private fun ExportSettingsDialog(
    exportIntent: Intent,
    onDismiss: () -> Unit,
    onResult: (ExportResult) -> Unit
) {
    val historyFlag = false

    var includeLogHashKey by remember { mutableStateOf(false) }
    var includeHistory by remember { mutableStateOf(false) }

    val fileSelectedLauncher = rememberFileSelectedLauncher {
        onResult(ExportResult(it, includeLogHashKey))
    }

    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Outlined.ImportExport,
                contentDescription = stringResource(id = R.string.export_settings)
            )
        },
        title = {
            Text(text = stringResource(id = R.string.export_settings), style = DialogTitleStyle)
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                item(key = R.string.export_include_recommendation, contentType = ContentType.TextItem) {
                    Text(text = stringResource(id = R.string.export_include_recommendation))
                }

                item(key = R.string.include_log_hash_key, contentType = ContentType.TextItem) {
                    CheckboxListItem(
                        checked = includeLogHashKey,
                        onCheckedChange = { includeLogHashKey= !includeLogHashKey},
                        position = ContentPosition.Leading,
                        headlineContent = textContent(R.string.include_log_hash_key),
                        otherContent = null
                    )
                }

                if (historyFlag) {
                    item(key = R.string.include_history, contentType = ContentType.TextItem) {
                        CheckboxListItem(
                            checked = includeHistory,
                            onCheckedChange = { includeHistory = !includeHistory },
                            position = ContentPosition.Leading,
                            headlineContent = textContent(R.string.include_log_hash_key),
                            otherContent = null
                        )
                    }
                }

                item(key = R.string.export_privacy, contentType = ContentType.TextItem) {
                    createAnnotatedString(id = R.string.export_privacy)
                }
            }
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            SaneAlertDialogTextButton(
                content = textContent(R.string.cancel),
                onClick = onDismiss
            )
        },
        confirmButton = {
            SaneAlertDialogTextButton(
                content = textContent(R.string.export_to_file),
                onClick = { fileSelectedLauncher.launch(exportIntent) }
            )
        }
    )
}
