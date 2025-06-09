package fe.linksheet.composable.page.settings.debug

import androidx.activity.compose.LocalActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.JoinLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.zwander.shared.ShizukuUtil
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.R
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.extension.android.showToast
import fe.linksheet.navigation.logViewerSettingsRoute
import fe.linksheet.module.viewmodel.DevSettingsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

fun groupSize(base: Int, vararg optional: Boolean): Int {
    return base + optional.count { it }
}

@Composable
fun NewDebugSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    viewModel: DevSettingsViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val coroutineScope = rememberCoroutineScope()
    val shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(context)) }
    val shizukuRunning by remember { mutableStateOf(ShizukuUtil.isShizukuRunning()) }

    val shizukuPermission by ShizukuUtil.rememberHasShizukuPermissionAsState()

    val feedback = LocalHapticFeedbackInteraction.current

    val disableLogging by viewModel.disableLogging.collectAsStateWithLifecycle()
    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.debug), onBackPressed = onBackPressed) {
        group(size = groupSize(3, viewModel.miuiCompatRequired, disableLogging)) {
            item(key = R.string.logs) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    headlineContent = textContent(R.string.logs),
                    supportingContent = textContent(R.string.logs_explainer),
                    icon = Icons.AutoMirrored.Outlined.List.iconPainter,
                    shape = shape,
                    padding = padding,
                    onClick = { navigate(logViewerSettingsRoute) }
                )
            }

            item(key = R.string.reset_app_link_verification_status) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    enabled = shizukuInstalled && shizukuRunning && shizukuPermission,
                    headlineContent = textContent(R.string.reset_app_link_verification_status),
                    supportingContent = textContent(R.string.reset_app_link_verification_status_subtitle),
                    icon = Icons.Outlined.RestartAlt.iconPainter,
                    shape = shape,
                    padding = padding,
                    onClick = viewModel::enqueueResetAppLinks
                )
            }


            if (viewModel.miuiCompatRequired) {
                item(key = "Audit MIUI environment") { padding, shape ->
                    DefaultTwoLineIconClickableShapeListItem(
                        headlineContent = text("Audit MIUI environment"),
                        icon = Icons.Rounded.BugReport.iconPainter,
                        shape = shape,
                        padding = padding,
                        onClick = {
                            feedback.copy(viewModel.auditMiuiEnvironment(), FeedbackType.Confirm)
                        }
                    )
                }
            }

            if (disableLogging) {
                item(key = "Delete all stored logs") { padding, shape ->
                    DefaultTwoLineIconClickableShapeListItem(
                        headlineContent = text("Delete all stored logs"),
                        icon = Icons.Rounded.DeleteForever.iconPainter,
                        shape = shape,
                        padding = padding,
                        onClick = {
                            coroutineScope.launch {
                                val counter = viewModel.deleteAllLogs()
                                activity?.showToast("Deleted $counter log sessions")
                            }
                        }
                    )
                }
            }

            item(key = "test") { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    enabled = false,
                    headlineContent = text("Database"),
                    icon = Icons.Rounded.JoinLeft.iconPainter,
                    shape = shape,
                    padding = padding,
                    onClick = {

                    }
                )
            }
        }
    }
}
