package app.linksheet.composable.page.settings.privacy

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
import app.linksheet.compose.theme.DialogTitleStyle
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContentWrapper
import fe.composekit.component.dialog.DialogDefaults
import fe.linksheet.R
import app.linksheet.compose.R as CommonR


@Composable
fun PrivacyIncognitoDeleteDataDialog(
    dismiss: () -> Unit,
    confirm: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Rounded.DeleteOutline,
                contentDescription = stringResource(id = R.string.settings_privacy_incognito_delete_dialog__title_delete_data)
            )
        },
        title = {
            Text(
                text = stringResource(id = R.string.settings_privacy_incognito_delete_dialog__title_delete_data),
                style = DialogTitleStyle
            )
        },
        text = {
            TextContentWrapper(
                modifier = Modifier.padding(bottom = DialogDefaults.ContentPadding),
                textContent = textContent(R.string.settings_privacy_incognito_delete_dialog__subtitle_delete_data)
            )
        },
        onDismissRequest = dismiss,
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text(text = stringResource(id = CommonR.string.generic__button_text_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = confirm) {
                Text(text = stringResource(id = CommonR.string.generic__button_text_delete))
            }
        }
    )
}
