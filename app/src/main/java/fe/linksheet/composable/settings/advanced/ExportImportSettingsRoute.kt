package fe.linksheet.composable.settings.advanced

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.android.compose.dialog.helper.dialogHelper
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.composable.util.Texts
import fe.linksheet.featureFlagSettingsRoute
import fe.linksheet.module.viewmodel.ExportSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExportImportSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: ExportSettingsViewModel = koinViewModel(),
) {
    val exportDialog = dialogHelper<Unit, Unit, Unit>(
        fetch = { },
        awaitFetchBeforeOpen = true,
        dynamicHeight = true
    ) { state, close ->
        ExportSettingsDialog(viewModel = viewModel)
    }

    val confirmImportDialog = dialogHelper<Uri, Uri, Unit>(
        fetch = { it },
        awaitFetchBeforeOpen = true,
        dynamicHeight = true
    ) { state, close ->
        ImportSettingsDialog(
            uri = state,
            close = close,
            viewModel = viewModel
        )
    }

    val importFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    confirmImportDialog.open(it)
                }
            }
        }
    )

    SettingsScaffold(R.string.export_import_settings, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
//            stickyHeader(key = "header") {
//                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
//                    PreferenceSubtitle(
//                        text = stringResource(id = R.string.feature_flags_explainer),
//                        paddingHorizontal = 10.dp
//                    )
//                }
//            }


            item(key = "export") {
                ClickableRow(
                    onClick = { exportDialog.open(Unit) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Texts(
                        headlineId = R.string.export,
                        subtitleId = R.string.export_explainer
                    )
                }
            }

            item(key = "import") {
                ClickableRow(
                    onClick = {
                        importFileLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(
                            Intent.CATEGORY_OPENABLE
                        ).apply { type = "application/json" })
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Texts(
                        headlineId = R.string.import_headline,
                        subtitleId = R.string.import_explainer
                    )
                }
            }
        }
    }
}

