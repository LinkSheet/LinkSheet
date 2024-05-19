package fe.linksheet.composable.settings.bottomsheet

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import fe.android.compose.dialog.helper.stateful.StatefulDialog
import fe.android.compose.dialog.helper.stateful.StatefulDialogState
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.TapConfig
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.icon.FilledIcon
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ContentPosition
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.PreferenceSwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.SwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.GroupValueProvider
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource
import fe.linksheet.extension.compose.ObserveStateChange
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.viewmodel.BottomSheetSettingsViewModel
import fe.linksheet.ui.LocalActivity
import org.koin.androidx.compose.koinViewModel


sealed class TapType(@StringRes val headline: Int, @StringRes val dialogTitle: Int) : GroupValueProvider<Int> {
    override val key: Int = headline

    data object Single :
        TapType(R.string.tap_customization_single_tap, R.string.tap_customization_dialog_title_single_tap)

    data object Double :
        TapType(R.string.tap_customization_double_tap, R.string.tap_customization_dialog_title_double_tap)

    data object Long : TapType(R.string.tap_customization_long_tap, R.string.tap_customization_dialog_title_long_tap)
}


@Composable
fun BottomSheetSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: BottomSheetSettingsViewModel = koinViewModel(),
) {
    val context = LocalActivity.current
    LocalLifecycleOwner.current.lifecycle.ObserveStateChange {
        if (!viewModel.usageStatsPermission.check()) {
            viewModel.usageStatsSorting(false)
        } else if (viewModel.wasTogglingUsageStatsSorting) {
            viewModel.usageStatsSorting(true)
            viewModel.wasTogglingUsageStatsSorting = false
        }
    }

    val tapTypePreferences = remember {
        mapOf(
            TapType.Single to viewModel.tapConfigSingle,
            TapType.Double to viewModel.tapConfigDouble,
            TapType.Long to viewModel.tapConfigLong
        )
    }

    // TODO: Pass data to onClose handler along result

    val tapConfigDialog = remember { StatefulDialogState<TapType, TapConfig>(TapType.Single) }
//    val tapConfigDialog = rememberStatefulDialog<TapType, Pair<TapType, TapConfig>>()
    StatefulDialog(
        state = tapConfigDialog,
        onClose = { type, newConfig -> tapTypePreferences[type]!!(newConfig) }
    ) { type ->
        TapConfigDialog(
            type = type,
            currentConfig = tapTypePreferences[type]!!.value,
            tapConfigDialog::dismiss,
            tapConfigDialog::close
        )
    }

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.bottom_sheet), onBackPressed = onBackPressed) {
        item(key = R.string.display_grid_layout, contentType = ContentTypeDefaults.SingleGroupItem) {
            PreferenceSwitchListItem(
                preference = viewModel.gridLayout,
                headlineContent = Resource.textContent(R.string.display_grid_layout),
                supportingContent = Resource.textContent(R.string.display_grid_layout_explainer),
            )
        }

        divider(stringRes = R.string.base_config)

        group(size = 4) {
            item(key = R.string.usage_stats_sorting) { padding, shape ->
                SwitchListItem(
                    shape = shape,
                    padding = padding,
                    checked = viewModel.usageStatsSorting(),
                    onCheckedChange = {
                        if (!viewModel.usageStatsPermission.check()) {
                            viewModel.usageStatsPermission.request(context)
                            viewModel.wasTogglingUsageStatsSorting = true
                        } else {
                            viewModel.usageStatsSorting(it)
                        }
                    },
                    position = ContentPosition.Trailing,
                    headlineContent = Resource.textContent(R.string.usage_stats_sorting),
                    supportingContent = Resource.textContent(R.string.usage_stats_sorting_explainer)
                )
            }

            item(key = R.string.enable_request_private_browsing_button) { padding, shape ->
                val browsers = remember {
                    KnownBrowser.browsers
                        .filter { it.privateBrowser }
                        .joinToString(separator = ", ") { it.displayName }
                }

                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.enableRequestPrivateBrowsingButton,
                    headlineContent = Resource.textContent(id = R.string.enable_request_private_browsing_button),
                    supportingContent = Resource.textContent(
                        id = R.string.enable_request_private_browsing_button_explainer,
                        browsers
                    )
                )
            }

            item(key = R.string.dont_show_filtered_item) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.dontShowFilteredItem,
                    headlineContent = Resource.textContent(R.string.dont_show_filtered_item),
                    supportingContent = Resource.textContent(R.string.dont_show_filtered_item_explainer),
                )
            }

            item(key = R.string.hide_bottom_sheet_choice_buttons) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.hideBottomSheetChoiceButtons,
                    headlineContent = Resource.textContent(R.string.hide_bottom_sheet_choice_buttons),
                    supportingContent = Resource.textContent(R.string.hide_bottom_sheet_choice_buttons_explainer),
                )
            }

//            item(key = R.string.show_native_label) { padding, shape ->
//                PreferenceSwitchListItem(
//                    shape = shape,
//                    padding = padding,
//                    preference = viewModel.bottomSheetNativeLabel,
//                    headlineContent = textContent(R.string.show_native_label),
//                    supportingContent = textContent(R.string.show_native_label_explainer),
//                )
//            }
        }

        divider(stringRes = R.string.tap_customization)

        group(size = 4) {
            items(map = tapTypePreferences) { type, pref, padding, shape ->
                var expanded by remember { mutableStateOf(false) }
                val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "Arrow rotation")

                ClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    onClick = {
                        expanded = true
                        tapConfigDialog.open(type)
                    },
                    role = Role.Button,
                    headlineContent = Resource.textContent(type.headline),
                    supportingContent = Resource.textContent(pref().id),
                    trailingContent = {
                        FilledIcon(
                            modifier = Modifier.rotate(rotation),
                            iconSize = 24.dp,
                            containerSize = 28.dp,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            imageVector = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null
                        )


//                        FilledTonalIconButton(onClick = {}) {
//                            Icon(imageVector = Icons.Outlined.Tune, contentDescription = stringResource(id = R.string.settings))
//                        }
////                        FilledIcon(
////                            imageVector = Icons.Outlined.Tune,
////                            contentDescription = stringResource(id = R.string.settings)
////                        )
                    }
                )
            }

            item(key = R.string.expand_on_app_select) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.expandOnAppSelect,
                    headlineContent = Resource.textContent(R.string.expand_on_app_select),
                    supportingContent = Resource.textContent(R.string.expand_on_app_select_explainer),
                )
            }
        }

        divider(stringRes = R.string.urlbar_settings)

        group(size = 3) {
            item(key = R.string.preview_url) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.previewUrl,
                    headlineContent = Resource.textContent(R.string.preview_url),
                    supportingContent = Resource.textContent(R.string.preview_url_explainer),
                )
            }

            item(key = R.string.hide_after_copying) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.hideAfterCopying,
                    headlineContent = Resource.textContent(R.string.hide_after_copying),
                    supportingContent = Resource.textContent(R.string.hide_after_copying_explainer),
                )
            }

            item(key = R.string.enable_ignore_libredirect_button) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.enableIgnoreLibRedirectButton,
                    headlineContent = Resource.textContent(R.string.enable_ignore_libredirect_button),
                    supportingContent = Resource.textContent(R.string.enable_ignore_libredirect_button_explainer),
                )
            }
        }
    }
}
