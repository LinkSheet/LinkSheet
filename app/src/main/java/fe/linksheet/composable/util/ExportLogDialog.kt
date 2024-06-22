package fe.linksheet.composable.util

import android.content.ClipboardManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.android.compose.dialog.helper.OnClose
import fe.linksheet.R
import fe.linksheet.component.list.base.ShapeListItemDefaults
import fe.linksheet.extension.android.setText
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.viewmodel.util.LogViewCommon

@Composable
fun ExportLogDialog(
    logViewCommon: LogViewCommon,
    clipboardManager: ClipboardManager,
    logEntries: List<LogEntry>,
    includeThrowable: Boolean = false,
    close: OnClose<Unit>,
) {
    val context = LocalContext.current

    var redactLog by remember { mutableStateOf(true) }
    var includeFingerprint by remember { mutableStateOf(true) }
    var includePreferences by remember { mutableStateOf(true) }
    var includeThrowableState by remember { mutableStateOf(includeThrowable) }

    val settings = remember(includeFingerprint, includePreferences, redactLog, includeThrowableState) {
        LogViewCommon.ExportSettings(includeFingerprint, includePreferences, redactLog, includeThrowableState)
    }

    val text = remember(settings, logEntries) {
        logViewCommon.buildExportText(context, settings, logEntries)
    }

    val coroutineScope = rememberCoroutineScope()

    DialogColumn {
        HeadlineText(headlineId = R.string.export_log)
        DialogSpacer()

        CheckboxRow(
            checked = redactLog,
            onClick = { redactLog = !redactLog },
            textId = R.string.redact_log
        )

        Spacer(modifier = Modifier.height(5.dp))

        if (logEntries.any { it is LogEntry.FatalEntry }) {
            CheckboxRow(
                checked = includeThrowableState,
                onClick = { includeThrowableState = !includeThrowableState },
                textId = R.string.include_throwable
            )

            Spacer(modifier = Modifier.height(5.dp))
        }


        CheckboxRow(
            checked = includeFingerprint,
            onClick = { includeFingerprint = !includeFingerprint },
            textId = R.string.include_fingerprint
        )

        Spacer(modifier = Modifier.height(5.dp))

        CheckboxRow(
            checked = includePreferences,
            onClick = { includePreferences = !includePreferences },
            textId = R.string.include_settings
        )

        Spacer(modifier = Modifier.height(5.dp))

        LinkableTextView(
            id = R.string.log_privacy,
            style = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        BottomRow {
//            TextButton(onClick = {
//                coroutineScope.launch(Dispatchers.IO) {
//                    val paste = logViewCommon.createPaste(text)
//                    clipboardManager.setText(
//                        context.resources.getString(R.string.log),
//                        paste ?: "Failure!"
//                    )
//
//                    close(Unit)
//                }
//            }) {
//                Text(text = stringResource(id = R.string.copy_to_clipboard))
//            }

            TextButton(
                onClick = {
                    clipboardManager.setText(context.resources.getString(R.string.log), text)
                    close(Unit)
                }
            ) {
                Text(text = stringResource(id = R.string.copy_to_clipboard))
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialogCheckboxRow(
    text: String,
    description: String,
    checked: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(ShapeListItemDefaults.SingleShape)
            .combinedClickable(
                onClick = onClick,
                role = Role.Checkbox
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onClick() }
        )

        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
