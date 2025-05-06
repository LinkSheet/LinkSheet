package fe.linksheet.composable.page.settings.advanced.exportimport

import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.util.BottomRow
import fe.linksheet.composable.util.DialogColumn
import fe.linksheet.composable.util.DialogSpacer
import fe.linksheet.composable.util.HeadlineText

@Composable
fun ImportSettingsDialog(importIntent: Intent, onResult: (FileSelectionResult) -> Unit) {
    val fileSelectedLauncher = rememberFileSelectedLauncher { onResult(it) }

    DialogColumn {
        HeadlineText(headlineId = R.string.import_settings)
        DialogSpacer()

        Text(text = stringResource(id = R.string.import_settings_override_preferences))

        Spacer(modifier = Modifier.height(10.dp))

        BottomRow {
            TextButton(onClick = { fileSelectedLauncher.launch(importIntent) }) {
                Text(text = stringResource(id = R.string.confirm_import))
            }
        }
    }
}

