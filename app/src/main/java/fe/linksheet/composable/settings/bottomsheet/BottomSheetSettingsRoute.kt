package fe.linksheet.composable.settings.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.PreferenceSubtitle
import fe.linksheet.composable.util.SettingEnabledCardColumn
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.extension.compose.ObserveStateChange
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.viewmodel.BottomSheetSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomSheetSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: BottomSheetSettingsViewModel = koinViewModel()
) {
    val context = LocalContext.currentActivity()
    LocalLifecycleOwner.current.lifecycle.ObserveStateChange {
        if (!viewModel.usageStatsPermission.check()) {
            viewModel.usageStatsSorting(false)
        } else if (viewModel.wasTogglingUsageStatsSorting) {
            viewModel.usageStatsSorting(true)
            viewModel.wasTogglingUsageStatsSorting = false
        }
    }

    SettingsScaffold(R.string.bottom_sheet, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "base_config") {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    PreferenceSubtitle(text = stringResource(R.string.base_config))

                    SettingEnabledCardColumn(
                        checked = viewModel.gridLayout(),
                        onChange = { viewModel.gridLayout(it) },
                        headline = stringResource(id = R.string.display_grid_layout),
                        subtitle = stringResource(id = R.string.display_grid_layout_explainer)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item(key = "usage_stats") {
                SwitchRow(
                    checked = viewModel.usageStatsSorting(),
                    onChange = {
                        if (!viewModel.usageStatsPermission.check()) {
                            viewModel.usageStatsPermission.request(context)
                            viewModel.wasTogglingUsageStatsSorting = true
                        } else {
                            viewModel.usageStatsSorting(it)
                        }
                    },
                    headlineId = R.string.usage_stats_sorting,
                    subtitleId = R.string.usage_stats_sorting_explainer
                )
            }

            item(key = "enable_request_private_browsing") {
                SwitchRow(
                    state = viewModel.enableRequestPrivateBrowsingButton,
                    headline = stringResource(id = R.string.enable_request_private_browsing_button),
                    subtitle = stringResource(
                        id = R.string.enable_request_private_browsing_button_explainer,
                        KnownBrowser.browsers.filter { it.privateBrowser }.joinToString(
                            separator = ", ",
                        ) { it.displayName }
                    )
                )
            }

            item(key = "dont_show_filtered_item") {
                SwitchRow(
                    state = viewModel.dontShowFilteredItem,
                    headlineId = R.string.dont_show_filtered_item,
                    subtitleId = R.string.dont_show_filtered_item_explainer
                )
            }

            item(key = "hide_choice_buttons") {
                SwitchRow(
                    state = viewModel.hideBottomSheetChoiceButtons,
                    headlineId = R.string.hide_bottom_sheet_choice_buttons,
                    subtitleId = R.string.hide_bottom_sheet_choice_buttons_explainer
                )
            }

            stickyHeader(key = "urlbar") {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    PreferenceSubtitle(text = stringResource(R.string.urlbar_settings))
                }
            }

            item(key = "preview_url") {
                SwitchRow(
                    state = viewModel.previewUrl,
                    headlineId = R.string.preview_url,
                    subtitleId = R.string.preview_url_explainer
                )
            }

            if (viewModel.previewUrl()) {
                item(key = "hide_after_copying") {
                    SwitchRow(
                        state = viewModel.hideAfterCopying,
                        headlineId = R.string.hide_after_copying,
                        subtitleId = R.string.hide_after_copying_explainer
                    )
                }

                item(key = "enable_ignore_libredirect") {
                    SwitchRow(
                        state = viewModel.enableIgnoreLibRedirectButton,
                        headlineId = R.string.enable_ignore_libredirect_button,
                        subtitleId = R.string.enable_ignore_libredirect_button_explainer
                    )
                }
            }
        }
    }
}

//@Preview(name = "BottomSheetSettingsRoutePreview", showBackground = true)
//@Composable
//private fun BottomSheetSettingsRoutePreview() {
//    val context = LocalContext.current
//
//
//    val pref = AppPreferenceRepository(context)
//    val viewModel = BottomSheetSettingsViewModel(context as Application, pref)
//
////    BottomSheetSettingsRoute(onBackPressed = {}, viewModel)
//}
