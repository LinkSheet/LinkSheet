package fe.linksheet.experiment.ui.overhaul.composable.page.settings.debug.log

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fe.android.compose.dialog.helper.stateful.StatefulDialogState
import fe.android.compose.dialog.helper.stateful.rememberStatefulDialog
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.dialog.DialogDefaults
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.experiment.ui.overhaul.composable.util.TextContentWrapper
import fe.linksheet.experiment.ui.overhaul.interaction.LocalHapticFeedbackInteraction
import fe.linksheet.module.log.file.LogFileService
import fe.linksheet.ui.HkGroteskFontFamily


@Composable
fun rememberDeleteLogDialog(
    logFile: LogFileService.LogFile
): StatefulDialogState<LogFileService.LogFile, LogFileService.LogFile> {
    val context = LocalContext.current
    val interaction = LocalHapticFeedbackInteraction.current


    val onClose: (LogFileService.LogFile) -> LogFileService.LogFile = {
        it
        //        interaction.copy(text, FeedbackType.Confirm)
    }

//    val state = rememberResultDialogState<LogViewCommon.ExportSettings>()

    val state = rememberStatefulDialog<LogFileService.LogFile, LogFileService.LogFile>(data = logFile)

    state.open()
//    StatefulDialog(state = state, onClose = onClose) {
//        DeleteLogDialog(
//            dismiss = interaction.wrap(state::dismiss, FeedbackType.Decline),
//            close = {
//                state.close()
//            },
//        )
//    }

    return state
}


@Composable
fun DeleteLogDialog(
    dismiss: () -> Unit,
    confirm: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Rounded.DeleteOutline,
                contentDescription = stringResource(id = R.string.delete_log_dialog__title_delete_log)
            )
        },
        title = {
            Text(
                text = stringResource(id = R.string.delete_log_dialog__title_delete_log),
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        },
        text = {
            TextContentWrapper(
                modifier = Modifier.padding(bottom = DialogDefaults.ContentPadding),
                textContent = textContent(R.string.delete_log_dialog__subtitle_delete_info)
            )
        },
        onDismissRequest = dismiss,
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text(text = stringResource(id = R.string.generic__button_text_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = confirm) {
                Text(text = stringResource(id = R.string.generic__button_text_delete))
            }
        }
    )
}
