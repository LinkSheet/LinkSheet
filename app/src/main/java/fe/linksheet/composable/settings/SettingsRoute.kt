package fe.linksheet.composable.settings

import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fe.linksheet.R
import fe.linksheet.appsWhichCanOpenLinksSettingsRoute
import fe.linksheet.composable.ClickableRow
import fe.linksheet.composable.SwitchRow
import fe.linksheet.extension.observeAsState
import fe.linksheet.preferredAppsSettingsRoute
import fe.linksheet.preferredBrowserSettingsRoute
import fe.linksheet.ui.theme.HkGroteskFontFamily

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(navController: NavController, viewModel: SettingsViewModel = viewModel()) {
    val context = LocalContext.current
    var defaultBrowserEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        defaultBrowserEnabled = !viewModel.checkDefaultBrowser(context)
    }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle
        .observeAsState(Lifecycle.Event.ON_RESUME)

    LaunchedEffect(lifecycleState.first) {
        if (lifecycleState.first == Lifecycle.Event.ON_RESUME) {
            defaultBrowserEnabled = !viewModel.checkDefaultBrowser(context)
            if (!viewModel.getUsageStatsAllowed(context)) {
                viewModel.onUsageStatsSorting(false)
            }

            if (viewModel.wasTogglingUsageStatsSorting) {
                viewModel.onUsageStatsSorting(true)
                viewModel.wasTogglingUsageStatsSorting = false
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = HkGroteskFontFamily,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }){ paddings ->
        LazyColumn(modifier = Modifier.padding(paddings), contentPadding = PaddingValues(5.dp)) {
            item(key = "open_default_browser") {
                ClickableRow(
                    padding = 10.dp,
                    onClick = { viewModel.openDefaultBrowserSettings(context) },
                    enabled = defaultBrowserEnabled
                ) {
                    Column {
                        Text(
                            text = stringResource(id = R.string.set_as_browser),
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(id = R.string.set_as_browser_explainer),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item(key = preferredBrowserSettingsRoute) {
                ClickableRow(
                    padding = 10.dp,
                    onClick = { navController.navigate(preferredBrowserSettingsRoute) }) {
                    Column {
                        Text(
                            text = stringResource(id = R.string.preferred_browser),
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(id = R.string.preferred_browser_explainer),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }


            item(key = preferredAppsSettingsRoute) {
                ClickableRow(
                    padding = 10.dp,
                    onClick = { navController.navigate(preferredAppsSettingsRoute) }) {
                    Column {
                        Text(
                            text = stringResource(id = R.string.preferred_apps),
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(id = R.string.preferred_apps_settings),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                item(key = appsWhichCanOpenLinksSettingsRoute) {
                    ClickableRow(
                        padding = 10.dp,
                        onClick = { navController.navigate(appsWhichCanOpenLinksSettingsRoute) }) {
                        Column {
                            Text(
                                text = stringResource(id = R.string.apps_which_can_open_links),
                                fontFamily = HkGroteskFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(id = R.string.apps_which_can_open_links_explainer_2),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            item(key = "usage_stats") {
                SwitchRow(
                    checked = viewModel.usageStatsSorting,
                    onChange = {
                        if (!viewModel.getUsageStatsAllowed(context)) {
                            viewModel.openUsageStatsSettings(context)
                        } else {
                            viewModel.onUsageStatsSorting(it)
                        }
                    },
                    headlineId = R.string.usage_stats_sorting,
                    subtitleId = R.string.usage_stats_sorting_explainer
                )
            }

            item(key = "enable_copy_button") {
                SwitchRow(
                    checked = viewModel.enableCopyButton,
                    onChange = {
                        viewModel.onEnableCopyButton(it)
                    },
                    headlineId = R.string.enable_copy_button,
                    subtitleId = R.string.enable_copy_button_explainer
                )
            }

            if (viewModel.enableCopyButton) {
                item(key = "hide_after_copying") {
                    SwitchRow(
                        checked = viewModel.hideAfterCopying,
                        onChange = {
                            viewModel.onHideAfterCopying(it)
                        },
                        headlineId = R.string.hide_after_copying,
                        subtitleId = R.string.hide_after_copying_explainer
                    )
                }
            }

            item(key = "enable_send_intent") {
                SwitchRow(
                    checked = viewModel.enableSendButton,
                    onChange = {
                        viewModel.onSendButton(it)
                    },
                    headlineId = R.string.enable_send_button,
                    subtitleId = R.string.enable_send_button_explainer
                )
            }

            item(key = "enable_single_tap") {
                SwitchRow(checked = viewModel.singleTap, onChange = {
                    viewModel.onSingleTap(it)
                }, headlineId = R.string.single_tap, subtitleId = R.string.single_tap_explainer)
            }

            item(key = "always_show_package_name") {
                SwitchRow(
                    checked = viewModel.alwaysShowPackageName,
                    onChange = {
                        viewModel.onAlwaysShowButton(it)
                    },
                    headlineId = R.string.always_show_package_name,
                    subtitleId = R.string.always_show_package_name_explainer
                )
            }

            item(key = "disable_toasts") {
                SwitchRow(
                    checked = viewModel.disableToasts,
                    onChange = { viewModel.onDisableToasts(it) },
                    headlineId = R.string.disable_toasts,
                    subtitleId = R.string.disable_toasts_explainer
                )
            }
        }
    }

}



