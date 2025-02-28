package fe.linksheet.composable.page.settings.bottomsheet

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.android.compose.dialog.helper.result.ResultDialog
import fe.android.compose.dialog.helper.result.rememberResultDialogState
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.version.AndroidVersion
import fe.android.preference.helper.compose.StateMappedPreference
import fe.composekit.component.ContentType
import fe.composekit.component.icon.FilledIcon
import fe.composekit.component.list.column.shape.ClickableShapeListItem
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.EnabledContent
import fe.composekit.component.list.item.type.SwitchListItem
import fe.composekit.layout.column.GroupValueProvider
import fe.linksheet.R
import fe.linksheet.navigation.Routes
import fe.linksheet.activity.bottomsheet.TapConfig
import fe.linksheet.composable.component.list.item.type.PreferenceDividedSwitchListItem
import fe.linksheet.composable.component.list.item.type.PreferenceSwitchListItem
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.composable.ui.LocalActivity
import fe.linksheet.extension.compose.ObserveStateChange
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.viewmodel.BottomSheetSettingsViewModel
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
    navigate: (String) -> Unit,
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

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.bottom_sheet), onBackPressed = onBackPressed) {
        item(key = R.string.display_grid_layout, contentType = ContentType.SingleGroupItem) {
            PreferenceSwitchListItem(
                preference = viewModel.gridLayout,
                headlineContent = textContent(R.string.display_grid_layout),
                supportingContent = textContent(R.string.display_grid_layout_explainer),
            )
        }

        divider(id = R.string.base_config)

        group(size = 4 + if (AndroidVersion.AT_LEAST_API_30_R) 1 else 0) {
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
                    headlineContent = textContent(R.string.usage_stats_sorting),
                    supportingContent = textContent(R.string.usage_stats_sorting_explainer)
                )
            }

            item(key = R.string.enable_request_private_browsing_button) { padding, shape ->
                val browsers = remember {
                    KnownBrowser.browsers.filter { it.privateBrowser }.joinToString(separator = ", ") { it.displayName }
                }

                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.enableRequestPrivateBrowsingButton,
                    headlineContent = textContent(id = R.string.enable_request_private_browsing_button),
                    supportingContent = textContent(
                        id = R.string.enable_request_private_browsing_button_explainer, browsers
                    )
                )
            }

            item(key = R.string.dont_show_filtered_item) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.dontShowFilteredItem,
                    headlineContent = textContent(R.string.dont_show_filtered_item),
                    supportingContent = textContent(R.string.dont_show_filtered_item_explainer),
                )
            }

            item(key = R.string.hide_bottom_sheet_choice_buttons) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.hideBottomSheetChoiceButtons,
                    headlineContent = textContent(R.string.hide_bottom_sheet_choice_buttons),
                    supportingContent = textContent(R.string.hide_bottom_sheet_choice_buttons_explainer),
                )
            }

            if (AndroidVersion.AT_LEAST_API_30_R) {
                item(key = R.string.switch_profile) { padding, shape ->
                    PreferenceDividedSwitchListItem(
                        enabled = if (viewModel.profileSwitcher.canQuickToggle()) EnabledContent.all else EnabledContent.Main.set,
                        shape = shape,
                        padding = padding,
                        preference = viewModel.bottomSheetProfileSwitcher,
                        onContentClick = {
                            navigate(Routes.ProfileSwitching)
                        },
                        headlineContent = textContent(R.string.switch_profile),
                        supportingContent = textContent(R.string.settings_bottom_sheet__text_profile_switcher),
                    )
                }
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

        divider(id = R.string.tap_customization)

        group(size = 4) {
            items(map = tapTypePreferences) { type, pref, padding, shape ->
                TapConfigGroupItem(tapTypePreferences[type]!!, type, pref, padding, shape)
            }

            item(key = R.string.expand_on_app_select) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.expandOnAppSelect,
                    headlineContent = textContent(R.string.expand_on_app_select),
                    supportingContent = textContent(R.string.expand_on_app_select_explainer),
                )
            }
        }

        divider(id = R.string.urlbar_settings)

        group(size = 3) {
            item(key = R.string.preview_url) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.previewUrl,
                    headlineContent = textContent(R.string.preview_url),
                    supportingContent = textContent(R.string.preview_url_explainer),
                )
            }

            item(key = R.string.hide_after_copying) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.hideAfterCopying,
                    headlineContent = textContent(R.string.hide_after_copying),
                    supportingContent = textContent(R.string.hide_after_copying_explainer),
                )
            }

            item(key = R.string.enable_ignore_libredirect_button) { padding, shape ->
                PreferenceSwitchListItem(
                    shape = shape,
                    padding = padding,
                    preference = viewModel.enableIgnoreLibRedirectButton,
                    headlineContent = textContent(R.string.enable_ignore_libredirect_button),
                    supportingContent = textContent(R.string.enable_ignore_libredirect_button_explainer),
                )
            }
        }
    }
}

@Composable
private fun TapConfigGroupItem(
    preference: StateMappedPreference<TapConfig, String>,
    type: TapType,
    pref: StateMappedPreference<TapConfig, String>,
    padding: PaddingValues,
    shape: Shape,
) {
    val interaction = LocalHapticFeedbackInteraction.current

    val state = rememberResultDialogState<TapConfig>()
    val rotation by animateFloatAsState(targetValue = if (state.isOpen) 180f else 0f, label = "Arrow rotation")

    ResultDialog(
        state = state, onClose = { newConfig ->
            preference(newConfig)
            interaction.perform(FeedbackType.Confirm)
        }) {
        TapConfigDialog(
            type = type,
            currentConfig = preference.value,
            onDismiss = interaction.wrap(FeedbackType.Decline, state::dismiss),
            state::close
        )
    }

    ClickableShapeListItem(
        shape = shape,
        padding = padding,
        onClick = state::open,
        role = Role.Button,
        headlineContent = textContent(type.headline),
        supportingContent = textContent(pref.value.id),
        trailingContent = {
            FilledIcon(
                modifier = Modifier.rotate(rotation),
                iconSize = 24.dp,
                containerSize = 28.dp,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                icon = Icons.Outlined.KeyboardArrowDown.iconPainter,
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

@Preview
@Composable
private fun BottomSheetSettingsRoutePreview() {

}
