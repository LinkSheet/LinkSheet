package fe.linksheet.composable.page.settings.advanced.exportimport

import android.content.Intent
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
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Composable
fun ExportSettingsDialog(exportIntent: Intent, onResult: (FileSelectionResult, Boolean) -> Unit) {
    val historyFlag = false
    val context = LocalContext.current

    var includeLogHashKey by remember { mutableStateOf(false) }
    var includeHistory by remember { mutableStateOf(false) }

    val fileSelectedLauncher = rememberFileSelectedLauncher {
        onResult(it, includeLogHashKey)
    }

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
                onClick = { fileSelectedLauncher.launch(exportIntent) }
            ) {
                Text(text = stringResource(id = R.string.export_to_file))
            }
        }
    }
}
