package fe.linksheet.composable.util

import android.content.ClipboardManager
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.android.compose.dialog.helper.OnClose
import fe.linksheet.R
import fe.linksheet.extension.android.setText
import fe.linksheet.module.viewmodel.util.LogViewCommon

@Composable
fun ExportLogDialog(
    logViewCommon: LogViewCommon,
    clipboardManager: ClipboardManager,
    close: OnClose<Unit>,
    log: (StringBuilder, Boolean) -> Unit
) {
    val context = LocalContext.current


    var redactLog by remember { mutableStateOf(true) }
    var includeFingerprint by remember { mutableStateOf(true) }
    var includePreferences by remember { mutableStateOf(true) }

    DialogColumn {
        HeadlineText(headlineId = R.string.export_log)
        DialogSpacer()

        CheckboxRow(
            checked = redactLog,
            onClick = { redactLog = !redactLog },
            textId = R.string.redact_log
        )

        Spacer(modifier = Modifier.height(5.dp))

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
            TextButton(
                onClick = {
                    clipboardManager.setText(
                        context.resources.getString(R.string.log),
                        logViewCommon.buildClipboardText(
                            includeFingerprint,
                            includePreferences,
                            redactLog,
                        ) { log(it, redactLog) }
                    )
                    close(Unit)
                }
            ) {
                Text(text = stringResource(id = R.string.copy_to_clipboard))
            }
        }
    }
}