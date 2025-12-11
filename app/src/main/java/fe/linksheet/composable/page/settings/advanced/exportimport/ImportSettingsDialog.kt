package fe.linksheet.composable.page.settings.advanced.exportimport

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.linksheet.compose.theme.DialogTitleStyle
import fe.android.compose.dialog.helper.result.ResultDialog
import fe.android.compose.dialog.helper.result.ResultDialogState
import fe.android.compose.dialog.helper.result.rememberResultDialogState
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.dialog.SaneAlertDialogTextButton
import fe.linksheet.R


@Composable
fun rememberImportSettingsDialog(
    importIntent: Intent,
    onResult: (FileSelectionResult) -> Unit
): ResultDialogState<FileSelectionResult> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberResultDialogState<FileSelectionResult>()

    ResultDialog(state = state, onClose = onResult) {
        ImportSettingsDialog(
            importIntent = importIntent,
            onDismiss = interaction.wrap(FeedbackType.Decline, state::dismiss),
            onResult = interaction.wrap(FeedbackType.Confirm, state::close),
        )
    }

    return state
}

@Composable
private fun ImportSettingsDialog(
    importIntent: Intent,
    onDismiss: () -> Unit,
    onResult: (FileSelectionResult) -> Unit
) {
    val fileSelectedLauncher = rememberFileSelectedLauncher { onResult(it) }
    val title = stringResource(id = R.string.import_settings)

    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = title
            )
        },
        title = {
            Text(
                text = title,
                style = DialogTitleStyle,
            )
        },
        text = {
            Text(text = stringResource(id = R.string.import_settings_override_preferences))
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
                content = textContent(R.string.confirm_import),
                onClick = { fileSelectedLauncher.launch(importIntent) }
            )
        }
    )
}

