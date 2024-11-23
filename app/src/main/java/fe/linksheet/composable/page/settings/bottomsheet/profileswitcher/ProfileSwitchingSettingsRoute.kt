package fe.linksheet.composable.page.settings.bottomsheet.profileswitcher

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fe.android.compose.icon.BitmapIconPainter.Companion.bitmap
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.card.AlertCard
import fe.composekit.component.icon.IconOffset
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.composekit.component.list.item.type.SwitchListItem
import fe.composekit.layout.column.group
import fe.linksheet.R
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.composable.ui.LocalActivity
import fe.linksheet.module.viewmodel.ProfileSwitchingSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ProfileSwitchingSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: ProfileSwitchingSettingsViewModel = koinViewModel(),
) {
    ProfileSwitchingSettingsRouteInternal(
        viewModel = viewModel,
        enabled = viewModel.enabled(),
        onEnable = { viewModel.enabled(it) },
        onBackPressed = onBackPressed
    )
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
private fun ProfileSwitchingSettingsRouteInternal(
    enabled: Boolean,
    onEnable: (Boolean) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: ProfileSwitchingSettingsViewModel,
) {
    val needsSetup by viewModel.needSetupFlow.collectAsStateWithLifecycle(false)
    val activity = LocalActivity.current

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings_profile_switcher__title_profile_switcher),
        onBackPressed = onBackPressed
    ) {
        if (needsSetup) {
            item(
                key = R.string.settings_profile_switcher__title_enable_cross_profile,
                contentType = ContentType.SingleGroupItem
            ) {
                AlertCard(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    icon = Icons.Rounded.WarningAmber.iconPainter,
                    iconOffset = IconOffset(y = (-1).dp),
                    iconContentDescription = stringResource(id = R.string.settings_profile_switcher__title_enable_cross_profile),
                    headline = textContent(R.string.settings_profile_switcher__title_enable_cross_profile),
                    subtitle = textContent(R.string.settings_profile_switcher__text_enable_cross_profile),
                    onClick = {
                        viewModel.profileSwitcher.launchCrossProfileInteractSettings(activity)
                    }
                )
            }
        } else {
            item(key = R.string.enabled, contentType = ContentType.SingleGroupItem) {
                SwitchListItem(
                    checked = enabled,
                    onCheckedChange = onEnable,
                    position = ContentPosition.Trailing,
                    headlineContent = textContent(R.string.switch_profile),
                    supportingContent = textContent(R.string.settings_bottom_sheet__text_profile_switcher),
                )
            }
        }

        divider(id = R.string.settings_profile_switcher__divider_current_profile)

        val profileSwitcher = viewModel.profileSwitcher
        val crossProfiles = profileSwitcher.getProfilesInternal() ?: emptyList()

        val userProfileInfo = viewModel.getUserProfileInfo()
        val isManagedProfile = viewModel.isManagedProfile()

        item(key = userProfileInfo.userHandle.identifier, contentType = ContentType.SingleGroupItem) {
            val textId = if (isManagedProfile) R.string.generic__label_work_profile else R.string.generic__label_personal_profile

            ProfileListItem(
                headlineContent = textContent(textId),
                supportingContent = textContent(R.string.settings_profile_switcher__text_current_profile),
                icon = Icons.Rounded.Person.iconPainter,
            )
        }

        divider(id = R.string.settings_profile_switcher__divider_other_profiles)

        group(list = userProfileInfo.otherHandles, key = { it.identifier }) { userHandle, padding, shape ->
            val crossProfile = remember(userHandle) { crossProfiles.firstOrNull { it.id == userHandle.identifier } }
            if (crossProfile != null) {
                val textId = if (isManagedProfile) R.string.generic__label_personal_profile else R.string.generic__label_work_profile

                DefaultTwoLineIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(textId),
                    supportingContent = textContent(R.string.settings_profile_switcher__text_other_profile),
                    icon = bitmap(crossProfile.bitmap),
                    onClick = {
                        viewModel.profileSwitcher.startOther(crossProfile, activity)
                    }
                )
            } else {
                ProfileListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.generic__label_unknown_profile),
                    supportingContent = textContent(R.string.settings_profile_switcher__text_not_installed),
                    icon = Icons.Rounded.Person.iconPainter,
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Preview
@Composable
private fun ProfileSwitchingSettingsRoutePreview() {
//    ProfileSwitchingSettingsRouteInternal(enabled = false, onEnable = {}, onBackPressed = {}, viewModel = viewModel)
}
