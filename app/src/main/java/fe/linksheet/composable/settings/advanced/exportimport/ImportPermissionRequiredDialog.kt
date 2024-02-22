package fe.linksheet.composable.settings.advanced.exportimport

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.android.compose.dialog.helper.OnClose
import fe.linksheet.R
import fe.linksheet.composable.util.*
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.module.preference.permission.PermissionBoundPreference

@Composable
fun ImportPermissionRequiredDialog(
    activity: Activity,
    permissions: List<PermissionBoundPreference>?,
    close: OnClose<Unit>
) {
    DialogColumn {
        HeadlineText(headlineId = R.string.permission_required)
        DialogSpacer()

        Text(text = stringResource(id = R.string.permission_required_explainer))

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn {
            permissions!!.forEach { permission ->
                item {
                    ClickableRow(onClick = { permission.request(activity) }) {
                        Column {
                            Text(text = stringResource(id = permission.title))
                            Text(text = stringResource(id = permission.explainer))
                        }
                    }


                }
            }
        }

        BottomRow {
            TextButton(onClick = {
//                uri?.let { close(viewModel.importPreferences(it)) }
            }) {
                Text(text = stringResource(id = R.string.confirm_import))
            }
        }
    }
}

