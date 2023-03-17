package fe.linksheet.composable.settings.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.extension.observeAsState


@Composable
fun BottomSheetSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val lifecycleState = LocalLifecycleOwner.current.lifecycle
        .observeAsState(ignoreFirst = Lifecycle.Event.ON_RESUME)

    LaunchedEffect(lifecycleState.first) {
        if (lifecycleState.first == Lifecycle.Event.ON_RESUME) {
            if (!viewModel.getUsageStatsAllowed(context)) {
                viewModel.onUsageStatsSorting(false)
            }

            if (viewModel.wasTogglingUsageStatsSorting) {
                viewModel.onUsageStatsSorting(true)
                viewModel.wasTogglingUsageStatsSorting = false
            }
        }
    }

    SettingsScaffold(R.string.bottom_sheet, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
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

            item(key = "dont_show_filtered_item"){
                SwitchRow(
                    checked = viewModel.dontShowFilteredItem,
                    onChange = { viewModel.onDontShowFilteredItem(it) },
                    headlineId = R.string.dont_show_filtered_item,
                    subtitleId = R.string.dont_show_filtered_item_explainer
                )
            }
        }
    }
}