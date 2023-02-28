package fe.linksheet.composable.settings

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
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
import fe.linksheet.util.Results
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(navController: NavController, viewModel: SettingsViewModel = viewModel()) {
    val context = LocalContext.current
    var defaultBrowserEnabled: Results<Boolean> by remember { mutableStateOf(Results.loading()) }

    LaunchedEffect(Unit) {
        delay(200)
        defaultBrowserEnabled = Results.boolean(viewModel.checkDefaultBrowser(context))
    }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle
        .observeAsState(ignoreFirst = Lifecycle.Event.ON_RESUME)

    LaunchedEffect(lifecycleState.first) {
        if (lifecycleState.first == Lifecycle.Event.ON_RESUME) {
            defaultBrowserEnabled = Results.loading()
            defaultBrowserEnabled = Results.boolean(viewModel.checkDefaultBrowser(context))

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
    }) { paddings ->
        LazyColumn(modifier = Modifier.padding(paddings), contentPadding = PaddingValues(5.dp)) {
            item(key = "open_default_browser") {
                val shouldUsePrimaryColor = defaultBrowserEnabled.isSuccess || defaultBrowserEnabled.isLoading
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor =
                        if (shouldUsePrimaryColor) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clickable {
                            viewModel.openDefaultBrowserSettings(context)
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        val color = if (shouldUsePrimaryColor) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onError

                        if (defaultBrowserEnabled.isLoading) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = color)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(10.dp))
                            Image(
                                imageVector = if (defaultBrowserEnabled.isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = if (defaultBrowserEnabled.isSuccess) "Checkmark" else "Error",
                                colorFilter = if (defaultBrowserEnabled.isSuccess) ColorFilter.tint(
                                    color
                                ) else ColorFilter.tint(
                                    color
                                )
                            )

                            Column(modifier = Modifier.padding(15.dp)) {
                                Text(
                                    text = stringResource(id = if (defaultBrowserEnabled.isSuccess) R.string.browser_status else R.string.set_as_browser),
                                    fontFamily = HkGroteskFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = color
                                )
                                Text(
                                    text = stringResource(id = if (defaultBrowserEnabled.isSuccess) R.string.set_as_browser_done else R.string.set_as_browser_explainer),
                                    color = if (defaultBrowserEnabled.isSuccess) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onError
                                )
                            }
                        }
                    }
                }
            }


            item(key = "divider_apps") {
                ItemDivider(id = R.string.apps)
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

            item(key = "divider_bottom_sheet") {
                ItemDivider(id = R.string.bottom_sheet)
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

            item(key = "grid_layout") {
                SwitchRow(
                    checked = viewModel.gridLayout,
                    onChange = { viewModel.onGridLayout(it) },
                    headlineId = R.string.display_grid_layout,
                    subtitleId = R.string.display_grid_layout_explainer
                )
            }
        }
    }
}

@Composable
fun ItemDivider(@StringRes id: Int) {
    Spacer(modifier = Modifier.height(10.dp))

    Text(
        modifier = Modifier.padding(horizontal = 10.dp),
        text = stringResource(id = id),
        fontFamily = HkGroteskFontFamily,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
    )

    Spacer(modifier = Modifier.height(5.dp))

}


