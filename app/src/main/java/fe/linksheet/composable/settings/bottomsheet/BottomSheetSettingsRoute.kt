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
import fe.linksheet.module.preference.Preferences


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
                viewModel.updateState(viewModel.usageStatsSorting, false)
            }

            if (viewModel.wasTogglingUsageStatsSorting) {
                viewModel.updateState(viewModel.usageStatsSorting, true)
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
                    checked = viewModel.usageStatsSorting.value,
                    onChange = {
                        if (!viewModel.getUsageStatsAllowed(context)) {
                            viewModel.openUsageStatsSettings(context)
                        } else {
                            viewModel.updateState(viewModel.usageStatsSorting, it)
                        }
                    },
                    headlineId = R.string.usage_stats_sorting,
                    subtitleId = R.string.usage_stats_sorting_explainer
                )
            }

            item(key = "enable_copy_button") {
                SwitchRow(
                    state = viewModel.enableCopyButton,
                    viewModel = viewModel,
                    headlineId = R.string.enable_copy_button,
                    subtitleId = R.string.enable_copy_button_explainer
                )
            }

            if (viewModel.enableCopyButton.value) {
                item(key = "hide_after_copying") {
                    SwitchRow(
                        state = viewModel.hideAfterCopying,
                        viewModel = viewModel,
                        headlineId = R.string.hide_after_copying,
                        subtitleId = R.string.hide_after_copying_explainer
                    )
                }
            }

            item(key = "enable_send_intent") {
                SwitchRow(
                    state = viewModel.enableSendButton,
                    viewModel = viewModel,
                    headlineId = R.string.enable_send_button,
                    subtitleId = R.string.enable_send_button_explainer
                )
            }

            if (viewModel.enableCopyButton.value || viewModel.enableSendButton.value) {
                item(key = "use_text_share_copy_buttons") {
                    SwitchRow(
                        state = viewModel.useTextShareCopyButtons,
                        viewModel = viewModel,
                        headlineId = R.string.use_text_share_copy_buttons,
                        subtitleId = R.string.use_text_share_copy_buttons_explainer
                    )
                }
            }

            item(key = "enable_single_tap") {
                SwitchRow(
                    state = viewModel.singleTap,
                    viewModel = viewModel,
                    headlineId = R.string.single_tap,
                    subtitleId = R.string.single_tap_explainer
                )
            }

            item(key = "always_show_package_name") {
                SwitchRow(
                    state = viewModel.alwaysShowPackageName,
                    viewModel = viewModel,
                    headlineId = R.string.always_show_package_name,
                    subtitleId = R.string.always_show_package_name_explainer
                )
            }

            item(key = "disable_toasts") {
                SwitchRow(
                    state = viewModel.disableToasts,
                    viewModel = viewModel,
                    headlineId = R.string.disable_toasts,
                    subtitleId = R.string.disable_toasts_explainer
                )
            }

            item(key = "grid_layout") {
                SwitchRow(
                    state = viewModel.gridLayout,
                    viewModel = viewModel,
                    headlineId = R.string.display_grid_layout,
                    subtitleId = R.string.display_grid_layout_explainer
                )
            }

            item(key = "dont_show_filtered_item") {
                SwitchRow(
                    state = viewModel.dontShowFilteredItem,
                    viewModel = viewModel,
                    headlineId = R.string.dont_show_filtered_item,
                    subtitleId = R.string.dont_show_filtered_item_explainer
                )
            }

            item(key = "preview_url") {
                SwitchRow(
                    state = viewModel.previewUrl,
                    viewModel = viewModel,
                    headlineId = R.string.preview_url,
                    subtitleId = R.string.preview_url_explainer
                )
            }
        }
    }
}