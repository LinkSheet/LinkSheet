package fe.linksheet.composable.page.settings.debug.log

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContentWrapper
import fe.composekit.component.dialog.DialogDefaults
import fe.linksheet.R
import app.linksheet.compose.theme.HkGroteskFontFamily


@Composable
fun DeleteLogDialog(
    dismiss: () -> Unit,
    confirm: () -> Unit,
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
