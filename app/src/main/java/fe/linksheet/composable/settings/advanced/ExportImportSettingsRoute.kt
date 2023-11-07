package fe.linksheet.composable.settings.advanced

//import fe.linksheet.module.viewmodel.FeatureFlagViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.zwander.shared.ShizukuUtil.rememberHasShizukuPermissionAsState
import fe.android.compose.dialog.helper.dialogHelper
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.ExportLogDialog
import fe.linksheet.composable.util.ExportSettingsDialog
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.composable.util.PreferenceSubtitle
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.composable.util.Texts
import fe.linksheet.module.log.AppLogger
import fe.linksheet.module.log.LogEntry
import fe.linksheet.module.viewmodel.FeatureFlagViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExportImportSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: FeatureFlagViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

//    var shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(activity)) }
//    var shizukuRunning by remember { mutableStateOf(isShizukuRunning()) }

    val exportDialog = dialogHelper<Unit, Unit, Unit>(
        fetch = { Unit },
        awaitFetchBeforeOpen = true,
        dynamicHeight = true
    ) { state, close ->
        ExportSettingsDialog(close = close)
    }




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
                    onClick = {  },
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