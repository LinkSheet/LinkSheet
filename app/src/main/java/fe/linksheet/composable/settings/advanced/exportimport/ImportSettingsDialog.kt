package fe.linksheet.composable.settings.advanced.exportimport

import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.android.compose.dialog.helper.OnClose
import fe.linksheet.R
import fe.linksheet.composable.util.BottomRow
import fe.linksheet.composable.util.DialogColumn
import fe.linksheet.composable.util.DialogSpacer
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.module.preference.permission.PermissionBoundPreference
import fe.linksheet.module.viewmodel.ExportSettingsViewModel

@Composable
fun ImportSettingsDialog(
    uri: Uri?,
    close: OnClose<Result<List<PermissionBoundPreference>>>,
    viewModel: ExportSettingsViewModel,
) {
    DialogColumn {
        HeadlineText(headlineId = R.string.import_settings)
        DialogSpacer()

        Text(text = stringResource(id = R.string.import_settings_override_preferences))

        Spacer(modifier = Modifier.height(10.dp))

        BottomRow {
            TextButton(onClick = {
                uri?.let { close(viewModel.importPreferences(it)) }
            }) {
                Text(text = stringResource(id = R.string.confirm_import))
            }
        }
    }
}

