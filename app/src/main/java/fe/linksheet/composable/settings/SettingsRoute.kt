package fe.linksheet.composable.settings

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.junkfood.seal.ui.component.BackButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    navController: NavController,
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    var defaultBrowserEnabled: Results<Boolean> by remember { mutableStateOf(Results.loading(false)) }

    LaunchedEffect(Unit) {
        delay(200)
        defaultBrowserEnabled = Results.booleanResult(viewModel.checkDefaultBrowser(context))
    }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle
        .observeAsState(ignoreFirst = Lifecycle.Event.ON_RESUME)

    LaunchedEffect(lifecycleState.first) {
        if (lifecycleState.first == Lifecycle.Event.ON_RESUME) {
            defaultBrowserEnabled = Results.loading(false)
            defaultBrowserEnabled = Results.booleanResult(viewModel.checkDefaultBrowser(context))

            if (!viewModel.getUsageStatsAllowed(context)) {
                viewModel.onUsageStatsSorting(false)
            }

            if (viewModel.wasTogglingUsageStatsSorting) {
                viewModel.onUsageStatsSorting(true)
                viewModel.wasTogglingUsageStatsSorting = false
            }
        }
    }


    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.settings),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }, navigationIcon = {
                    BackButton {
                        onBackPressed()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, content = { padding ->
            LazyColumn(modifier = Modifier.padding(padding), contentPadding = PaddingValues(5.dp)) {
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
        })
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


