package fe.linksheet.experiment.ui.overhaul.composable.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.android.compose.dialog.helper.result.ResultDialog
import fe.android.compose.dialog.helper.result.ResultDialogState
import fe.android.compose.dialog.helper.result.rememberResultDialogState
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ContentPosition
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.CheckboxListItem
import fe.linksheet.experiment.ui.overhaul.composable.util.AnnotatedStringResource.Companion.annotated
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.experiment.ui.overhaul.composable.util.TextContentWrapper
import fe.linksheet.experiment.ui.overhaul.interaction.FeedbackType
import fe.linksheet.experiment.ui.overhaul.interaction.LocalHapticFeedbackInteraction
import fe.linksheet.experiment.ui.overhaul.interaction.wrap
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.viewmodel.util.LogViewCommon
import fe.linksheet.ui.HkGroteskFontFamily

@Composable
fun rememberNewExportLogDialog(
    logViewCommon: LogViewCommon,
    name: String,
    fnLogEntries: () -> List<LogEntry>,
): ResultDialogState<LogViewCommon.ExportSettings> {
    val context = LocalContext.current
    val interaction = LocalHapticFeedbackInteraction.current
    val logEntries by remember(fnLogEntries) {
        lazy(fnLogEntries)
    }

    val onClose: (LogViewCommon.ExportSettings) -> Unit = { settings ->
        val text = logViewCommon.buildExportText(context, settings, logEntries)
        interaction.copy(text, FeedbackType.Confirm)
    }

    val state = rememberResultDialogState<LogViewCommon.ExportSettings>()

    ResultDialog(state = state, onClose = onClose) {
        val isFatal = remember(logEntries) {
            logEntries.any { it is LogEntry.FatalEntry }
        }

        NewExportLogDialog(
            name = name,
            isFatal = isFatal,
            dismiss = interaction.wrap(state::dismiss, FeedbackType.Decline),
            close = state::close
        )
    }

    return state
}


@Composable
private fun NewExportLogDialog(
    name: String,
    isFatal: Boolean = false,
    dismiss: () -> Unit,
    close: (LogViewCommon.ExportSettings) -> Unit
) {
    var redactLog by remember { mutableStateOf(true) }
    var includeFingerprint by remember { mutableStateOf(true) }
    var includePreferences by remember { mutableStateOf(true) }
    var includeThrowableState by remember { mutableStateOf(isFatal) }

    val settings = remember(includeFingerprint, includePreferences, redactLog, includeThrowableState) {
        LogViewCommon.ExportSettings(includeFingerprint, includePreferences, redactLog, includeThrowableState)
    }

    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = stringResource(id = R.string.export_log_dialog__title_share_logs)
            )
        },
        title = {
            Text(
                text = stringResource(id = R.string.export_log_dialog__title_share_logs),
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                item(key = R.string.export_log_dialog__text_log_info, contentType = ContentTypeDefaults.TextRow) {
                    TextContentWrapper(
                        modifier = Modifier.padding(bottom = DialogDefaults.ContentPadding),
                        textContent = annotated(R.string.export_log_dialog__text_log_info, name)
                    )
                }

                item(
                    key = R.string.export_log_dialog__title_redact_log,
                    contentType = ContentTypeDefaults.CheckboxRow
                ) {
                    CheckboxListItem(
                        checked = redactLog,
                        onCheckedChange = { redactLog = it },
                        position = ContentPosition.Leading,
                        headlineContent = textContent(R.string.export_log_dialog__title_redact_log),
                        supportingContent = textContent(R.string.export_log_dialog__subtitle_redact_log),
                        otherContent = null,
                        innerPadding = DialogDefaults.ListItemInnerPadding,
                        textOptions = DialogDefaults.ListItemTextOptions
                    )
                }

                if (isFatal) {
                    item(
                        key = R.string.export_log_dialog__title_include_throwable,
                        contentType = ContentTypeDefaults.CheckboxRow
                    ) {
                        CheckboxListItem(
                            checked = includeThrowableState,
                            onCheckedChange = { includeThrowableState = it },
                            position = ContentPosition.Leading,
                            headlineContent = textContent(R.string.export_log_dialog__title_include_throwable),
                            supportingContent = textContent(R.string.export_log_dialog__subtitle_include_throwable),
                            otherContent = null,
                            innerPadding = DialogDefaults.ListItemInnerPadding,
                            textOptions = DialogDefaults.ListItemTextOptions
                        )
                    }
                }

                item(
                    key = R.string.export_log_dialog__title_include_fingerprint,
                    contentType = ContentTypeDefaults.CheckboxRow
                ) {
                    CheckboxListItem(
                        checked = includeFingerprint,
                        onCheckedChange = { includeFingerprint = it },
                        position = ContentPosition.Leading,
                        headlineContent = textContent(R.string.export_log_dialog__title_include_fingerprint),
                        supportingContent = textContent(R.string.export_log_dialog__subtitle_include_fingerprint),
                        otherContent = null,
                        innerPadding = DialogDefaults.ListItemInnerPadding,
                        textOptions = DialogDefaults.ListItemTextOptions
                    )
                }

                item(
                    key = R.string.export_log_dialog__title_include_settings,
                    contentType = ContentTypeDefaults.CheckboxRow
                ) {
                    CheckboxListItem(
                        checked = includePreferences,
                        onCheckedChange = { includePreferences = it },
                        position = ContentPosition.Leading,
                        headlineContent = textContent(R.string.export_log_dialog__title_include_settings),
                        supportingContent = textContent(R.string.export_log_dialog__subtitle_include_settings),
                        otherContent = null,
                        innerPadding = DialogDefaults.ListItemInnerPadding,
                        textOptions = DialogDefaults.ListItemTextOptions
                    )
                }

                item(key = R.string.export_log_dialog__text_log_privacy, contentType = ContentTypeDefaults.TextRow) {
                    TextContentWrapper(
                        modifier = Modifier.padding(top = DialogDefaults.ContentPadding),
                        textContent = annotated(R.string.export_log_dialog__text_log_privacy)
                    )
                }
            }
        },
        onDismissRequest = dismiss,
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text(text = stringResource(id = R.string.generic__button_text_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = { close(settings) }) {
                Text(text = stringResource(id = R.string.generic__button_text_copy_clipboard))
            }
        }
    )
}
