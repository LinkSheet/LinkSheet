package fe.linksheet.composable.settings.bottomsheet

import android.app.Application
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.*
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.extension.compose.ObserveStateChange
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.viewmodel.BottomSheetSettingsViewModel
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.LocalKoinApplication


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomSheetSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: BottomSheetSettingsViewModel = koinViewModel()
) {
    val context = LocalContext.currentActivity()
    LocalLifecycleOwner.current.lifecycle.ObserveStateChange {
        if (!viewModel.getUsageStatsAllowed(context)) {
            viewModel.updateState(viewModel.usageStatsSorting, false)
        } else if (viewModel.wasTogglingUsageStatsSorting) {
            viewModel.updateState(viewModel.usageStatsSorting, true)
            viewModel.wasTogglingUsageStatsSorting = false
        }
    }

    SettingsScaffold(R.string.bottom_sheet, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxHeight(),
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
                    checked = viewModel.usageStatsSorting.value,
                    onChange = {
                        if (!viewModel.getUsageStatsAllowed(context)) {
                            viewModel.openUsageStatsSettings(context)
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
                    viewModel = viewModel,
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
                    viewModel = viewModel,
                    headlineId = R.string.dont_show_filtered_item,
                    subtitleId = R.string.dont_show_filtered_item_explainer
                )
            }

            item(key = "hide_choice_buttons") {
                SwitchRow(
                    state = viewModel.hideBottomSheetChoiceButtons,
                    viewModel = viewModel,
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
                    viewModel = viewModel,
                    headlineId = R.string.preview_url,
                    subtitleId = R.string.preview_url_explainer
                )
            }

            if(viewModel.previewUrl()){
                item(key = "hide_after_copying") {
                    SwitchRow(
                        state = viewModel.hideAfterCopying,
                        viewModel = viewModel,
                        headlineId = R.string.hide_after_copying,
                        subtitleId = R.string.hide_after_copying_explainer
                    )
                }

                item(key = "enable_ignore_libredirect") {
                    SwitchRow(
                        state = viewModel.enableIgnoreLibRedirectButton,
                        viewModel = viewModel,
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
