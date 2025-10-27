package fe.linksheet.debug.composable

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.linksheet.compose.DebugMenuButton
import app.linksheet.feature.shizuku.shizukuDebugItem
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.activity.onboarding.OnboardingActivity
import fe.linksheet.composable.page.settings.privacy.remoteconfig.rememberRemoteConfigDialog
import fe.linksheet.debug.activity.ComponentStateActivity
import fe.linksheet.debug.activity.ComposableRendererActivity
import fe.linksheet.debug.activity.DebugActivity
import fe.linksheet.debug.activity.ExportLogDialogTestActivity
import fe.linksheet.debug.activity.LinkTestingActivity
import fe.linksheet.debug.activity.LocaleDebugActivity
import fe.linksheet.debug.activity.WorkManagerActivity
import fe.linksheet.debug.module.viewmodel.DebugViewModel
import fe.linksheet.extension.compose.dashedBorder
import fe.linksheet.navigation.Routes
import kotlin.reflect.KClass

@Composable
fun DebugMenuSlot(viewModel: DebugViewModel, navigate: (String) -> Unit) {
    val activity = LocalActivity.current

    Column(
        modifier = Modifier
            .dashedBorder(1.dp, Color.Gray, 12.dp)
            .padding(all = 2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            shizukuDebugItem()

            if (activity != null) {
                item(key = "start-service") {
                    DebugMenuButton(
                        text = "Start service",
                        onClick = {
//                            val intent = Intent(activity, SocketService::class.java)
//                            if (AndroidVersion.isAtLeastApi26O()) {
//                                activity.startForegroundService(intent)
//                            }
                        }
                    )
                }
            }
            item(key = "remote-config-dialog") {
                val result = rememberRemoteConfigDialog {
                }

                DebugMenuButton(
                    text = "Remote config dialog",
                    onClick = {
                        result.open()
                    }
                )
            }

            if (activity != null) {
                item(key = "remoteconfig-assets") {
                    FilledTonalActivityLauncher(
                        activity = activity,
                        text = "Work manager",
                        intent = createIntent(activity, WorkManagerActivity::class)
                    )
                }
                item(key = "component-state") {
                    FilledTonalActivityLauncher(
                        activity = activity,
                        text = "Component state",
                        intent = createIntent(activity, ComponentStateActivity::class)
                    )
                }

                item(key = "locale") {
                    FilledTonalActivityLauncher(
                        activity = activity,
                        text = "Locale",
                        intent = createIntent(activity, LocaleDebugActivity::class)
                    )
                }
            }

            item(key = "draw-borders") {
                val drawBorders by viewModel.drawBorders.collectAsStateWithLifecycle()

                DebugMenuButton(
                    text = "Draw borders ($drawBorders)",
                    onClick = { viewModel.drawBorders(!drawBorders) }
                )
            }

            item(key = "crash") {
                FilledTonalButton(
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    onClick = {
                        throw Exception("Crash")
                    }
                ) {
                    Text(text = "Crash")
                }
            }

            if (viewModel.debugMiuiCompatProvider != null) {
                item(key = "miui") {
                    var isRequired by remember {
                        mutableStateOf(viewModel.debugMiuiCompatProvider.isRequired.value)
                    }

                    DebugMenuButton(
                        text = "Toggle Miui ($isRequired)",
                        onClick = {
                            isRequired = viewModel.toggleMiuiCompatRequired()
                        }
                    )
                }
            }

            item(key = "rules") {
                FilledTonalButton(
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    onClick = { navigate(Routes.RuleOverview) }
                ) {
                    Text(text = "Rules")
                }
            }

            if (activity != null) {
                item(key = "onboarding") {
                    FilledTonalActivityLauncher(
                        activity = activity,
                        text = "Launch onboarding",
                        intent = createIntent(activity, OnboardingActivity::class)
                    )
                }

                item(key = "export_log_dialog") {
                    FilledTonalActivityLauncher(
                        activity = activity,
                        text = "Export log dialog testing",
                        intent = createIntent(activity, ExportLogDialogTestActivity::class)
                    )
                }

                item(key = "link_menu") {
                    FilledTonalActivityLauncher(
                        activity = activity,
                        text = "Link testing",
                        intent = createIntent(activity, LinkTestingActivity::class)
                    )
                }

                item(key = "snap_tester") {
                    FilledTonalActivityLauncher(
                        activity = activity,
                        text = "Snap tester",
                        intent = createIntent(activity, DebugActivity::class)
                    )
                }

                item(key = "url_preview") {
                    FilledTonalActivityLauncher(
                        activity = activity,
                        text = "Url preview",
                        intent = createIntent(activity, ComposableRendererActivity::class)
                    )
                }
            }
        }
    }
}

private fun createIntent(activity: Activity, activityClass: KClass<*>): Intent {
    return Intent(activity, activityClass.java)
}

@Composable
private fun FilledTonalActivityLauncher(activity: Activity, text: String, intent: Intent) {
    DebugMenuButton(
        text = text,
        onClick = { activity.startActivity(intent) }
    )
}
