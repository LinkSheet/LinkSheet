package fe.linksheet.experiment.ui.overhaul.composable.page.settings.debug

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.zwander.shared.ShizukuUtil
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.default.DefaultIconClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.component.util.Resource.Companion.textContent
import fe.linksheet.logViewerSettingsRoute
import fe.linksheet.module.viewmodel.DevSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun NewDebugSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    viewModel: DevSettingsViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(context)) }
    val shizukuRunning by remember { mutableStateOf(ShizukuUtil.isShizukuRunning()) }

    val shizukuPermission by ShizukuUtil.rememberHasShizukuPermissionAsState()

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.debug), onBackPressed = onBackPressed) {
        group(2) {
            item(key = R.string.logs) { padding, shape ->
                DefaultIconClickableShapeListItem(
                    headlineContent = textContent(R.string.logs),
                    supportingContent = textContent(R.string.logs_explainer),
                    icon = Icons.AutoMirrored.Outlined.List,
                    shape = shape,
                    padding = padding,
                    onClick = { navigate(logViewerSettingsRoute) }
                )
            }

            item(key = R.string.reset_app_link_verification_status) { padding, shape ->
                DefaultIconClickableShapeListItem(
                    enabled = shizukuInstalled && shizukuRunning && shizukuPermission,
                    headlineContent = textContent(R.string.reset_app_link_verification_status),
                    supportingContent = textContent(R.string.reset_app_link_verification_status_subtitle),
                    icon = Icons.Outlined.RestartAlt,
                    shape = shape,
                    padding = padding,
                    onClick = viewModel::enqueueResetAppLinks
                )
            }
        }
    }
}
