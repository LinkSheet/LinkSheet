package fe.linksheet.composable.page.settings.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.toEnabledContentSet
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.R
import fe.linksheet.module.viewmodel.BottomSheetActionBarSettingsViewModel
import org.koin.androidx.compose.koinViewModel
import app.linksheet.compose.R as CommonR

@Composable
fun BottomSheetActionBarSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: BottomSheetActionBarSettingsViewModel = koinViewModel(),
) {
    val enableActionBar by viewModel.enable.collectAsStateWithLifecycle()
    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings_bottom_sheet_action_bar__title_action_bar),
        onBackPressed = onBackPressed
    ) {
        item(key = R.string.settings_bottom_sheet_action_bar__title_enable_action_bar, contentType = ContentType.SingleGroupItem) {
            PreferenceSwitchListItem(
                statePreference = viewModel.enable,
                headlineContent = textContent(R.string.settings_bottom_sheet_action_bar__title_enable_action_bar),
                supportingContent = textContent(R.string.settings_bottom_sheet_action_bar__subtitle_enable_action_bar),
            )
        }
        divider(id = R.string.settings_bottom_sheet_action_bar__divider_actions)

        group(size = 2) {
            item(key = R.string.enable_ignore_libredirect_button) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = enableActionBar.toEnabledContentSet(),
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.enableIgnoreLibRedirectButton,
                    headlineContent = textContent(R.string.enable_ignore_libredirect_button),
                    supportingContent = textContent(R.string.enable_ignore_libredirect_button_explainer),
                )
            }

            item(key = R.string.settings_bottom_sheet__title_enable_open_externally_button) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = enableActionBar.toEnabledContentSet(),
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.enableOpenExternallyButton,
                    headlineContent = textContent(R.string.settings_bottom_sheet__title_enable_open_externally_button),
                    supportingContent = textContent(R.string.settings_bottom_sheet__subtitle_enable_open_externally_button),
                )
            }
        }

        divider(id = CommonR.string.options)

        group(size = 2) {
            item(key = R.string.hide_after_copying) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = enableActionBar.toEnabledContentSet(),
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.hideAfterCopying,
                    headlineContent = textContent(R.string.hide_after_copying),
                    supportingContent = textContent(R.string.hide_after_copying_explainer),
                )
            }

            item(key = R.string.settings_bottom_sheet__title_double_tap_url) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = enableActionBar.toEnabledContentSet(),
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.doubleTapUrl,
                    headlineContent = textContent(R.string.settings_bottom_sheet__title_double_tap_url),
                    supportingContent = textContent(R.string.settings_bottom_sheet__subtitle_double_tap_url),
                )
            }
        }
    }
}
