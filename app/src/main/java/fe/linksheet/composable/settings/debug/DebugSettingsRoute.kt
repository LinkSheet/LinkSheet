package fe.linksheet.composable.settings.debug

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.zwander.shared.ShizukuUtil
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.composable.util.Texts
import fe.linksheet.logViewerSettingsRoute
import fe.linksheet.module.viewmodel.DevSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun DebugSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: DevSettingsViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(context)) }
    val shizukuRunning by remember { mutableStateOf(ShizukuUtil.isShizukuRunning()) }

    val shizukuPermission by ShizukuUtil.rememberHasShizukuPermissionAsState()

    val shizukuMode = shizukuInstalled && shizukuRunning && shizukuPermission


    SettingsScaffold(R.string.debug, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item(key = "logs") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = logViewerSettingsRoute,
                    headlineId = R.string.logs,
                    subtitleId = R.string.logs_explainer,
                    image = {
                        ColoredIcon(icon = Icons.Default.List, descriptionId = R.string.logs)
                    }
                )
            }

            item(key = "reset_verification_status") {
                ClickableRow(
                    enabled = shizukuMode,
                    verticalAlignment = Alignment.CenterVertically,
                    onClick = viewModel::enqueueResetAppLinks
                ) {
                    Texts(
                        headlineId = R.string.reset_app_link_verification_status,
                        subtitleId = R.string.reset_app_link_verification_status_subtitle
                    )
                }
            }


//            item(key = "load_dumped_preferences") {
//                SettingsItemRow(
//                    navController = navController,
//                    navigateTo = loadDumpedPreferences,
//                    headlineId = R.string.import_dumped_preference,
//                    subtitleId = R.string.import_dumped_preference_explainer,
//                    image = {
//                        ColoredIcon(icon = Icons.Default.List, descriptionId = R.string.logs)
//                    }
//                )
//            }
        }
    }
}
