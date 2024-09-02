package fe.linksheet.composable.page.settings.advanced.exportimport

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.composable.util.*
import fe.linksheet.module.viewmodel.ExportSettingsViewModel
import java.time.LocalDateTime

@Composable
fun ExportSettingsDialog(viewModel: ExportSettingsViewModel) {
    val historyFlag = false

    val context = LocalContext.current
    val contentResolver = context.contentResolver

    var includeLogHashKey by remember { mutableStateOf(false) }
    var includeHistory by remember { mutableStateOf(false) }

    val fileSelectedLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri -> viewModel.exportPreferences(uri, includeLogHashKey) }
            }
        }
    )

    DialogColumn {
        HeadlineText(headlineId = R.string.export_settings)
        DialogSpacer()

        Text(text = stringResource(id = R.string.export_include_recommendation))

        CheckboxRow(
            checked = includeLogHashKey,
            onClick = { includeLogHashKey = !includeLogHashKey },
            textId = R.string.include_log_hash_key
        )

        if (historyFlag) {
            Spacer(modifier = Modifier.height(5.dp))

            CheckboxRow(
                checked = includeHistory,
                onClick = { includeHistory = !includeHistory },
                textId = R.string.include_history
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        LinkableTextView(
            id = R.string.export_privacy,
            style = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        BottomRow {
            TextButton(
                onClick = {
                    fileSelectedLauncher.launch(
                        Intent(Intent.ACTION_CREATE_DOCUMENT)
                            .addCategory(Intent.CATEGORY_OPENABLE)
                            .putExtra(
                                Intent.EXTRA_TITLE,
                                context.getString(R.string.export_file_name, LocalDateTime.now())
                            ).apply {
                                type = "application/json"
                            }
                    )
                }
            ) {
                Text(text = stringResource(id = R.string.export_to_file))
            }
        }
    }
}
