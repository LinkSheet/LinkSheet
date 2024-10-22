package fe.linksheet.composable.page.settings.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.SwitchListItem
import fe.linksheet.R
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.module.viewmodel.ProfileSwitchingSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileSwitchingSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: ProfileSwitchingSettingsViewModel = koinViewModel(),
) {
    ProfileSwitchingSettingsRouteInternal(
        enabled = viewModel.enabled(),
        onEnable = { viewModel.enabled(it) },
        onBackPressed = onBackPressed
    )
}

@Composable
private fun ProfileSwitchingSettingsRouteInternal(
    enabled: Boolean,
    onEnable: (Boolean) -> Unit,
    onBackPressed: () -> Unit,
) {
    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings_profile_switcher__title_profile_switcher),
        onBackPressed = onBackPressed
    ) {
        item(key = R.string.enabled, contentType = ContentType.SingleGroupItem) {
            SwitchListItem(
                checked = enabled,
                onCheckedChange = onEnable,
                position = ContentPosition.Trailing,
                headlineContent = textContent(R.string.switch_profile),
                supportingContent = textContent(R.string.settings_bottom_sheet__text_profile_switcher),
            )
        }

        divider(id = R.string.settings_profile_switcher__divider_configuration)


    }
}

@Preview
@Composable
private fun ProfileSwitchingSettingsRoutePreview() {
    ProfileSwitchingSettingsRouteInternal(enabled = false, onEnable = {}, onBackPressed = {})
}
