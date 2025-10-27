package fe.linksheet.composable.page.settings.bottomsheet

import android.app.Activity
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.compose.preview.PreviewContainer
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
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.R
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.linksheet.extension.android.toImageBitmap
import fe.linksheet.feature.profile.CrossProfile
import fe.linksheet.feature.profile.ProfileStatus
import fe.linksheet.feature.profile.UserProfileInfo
import fe.linksheet.module.viewmodel.ProfileSwitchingSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ProfileSwitchingSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: ProfileSwitchingSettingsViewModel = koinViewModel(),
) {
    val enabled by viewModel.enabled.collectAsStateWithLifecycle()
    val status by viewModel.status.collectAsStateWithLifecycle(ProfileStatus.Unsupported)
    val userProfileInfo by viewModel.userProfileInfo.collectAsStateWithLifecycle(null)

    ProfileSwitchingSettingsRouteInternal(
        status = status,
        userProfileInfo = userProfileInfo,
        isManagedProfile = viewModel.checkIsManagedProfile(),
        enabled = enabled,
        onEnable = viewModel.enabled,
        onBackPressed = onBackPressed,
        launchCrossProfileInteractSettings = viewModel::launchCrossProfileInteractSettings,
        startOther = viewModel::startOther
    )
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
private fun ProfileSwitchingSettingsRouteInternal(
    status: ProfileStatus,
    userProfileInfo: UserProfileInfo?,
    isManagedProfile: Boolean,
    enabled: Boolean,
    onEnable: (Boolean) -> Unit,
    onBackPressed: () -> Unit,
    launchCrossProfileInteractSettings: (Activity?) -> Unit,
    startOther: (CrossProfile, Activity?) -> Unit,
) {
    val activity = LocalActivity.current

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings_profile_switcher__title_profile_switcher),
        onBackPressed = onBackPressed
    ) {
        when (status) {
            is ProfileStatus.Available -> {

            }

            ProfileStatus.NoProfiles -> {
                item(
                    key = R.string.settings_profile_switcher__title_no_profile,
                    contentType = ContentType.SingleGroupItem
                ) {
                    AlertCard(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        icon = Icons.Rounded.WarningAmber.iconPainter,
                        iconOffset = IconOffset(y = (-1).dp),
                        iconContentDescription = stringResource(id = R.string.settings_profile_switcher__title_no_profile),
                        headline = textContent(R.string.settings_profile_switcher__title_no_profile),
                        subtitle = textContent(R.string.settings_profile_switcher__text_no_profile),
                        onClick = {
                        }
                    )
                }
            }

            ProfileStatus.NotConnected -> {
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
                            launchCrossProfileInteractSettings(activity)
                        }
                    )
                }
            }

            ProfileStatus.Unsupported -> {

            }
        }

        if (status is ProfileStatus.Available) {
            item(key = R.string.switch_profile, contentType = ContentType.SingleGroupItem) {
                SwitchListItem(
                    checked = enabled,
                    onCheckedChange = onEnable,
                    position = ContentPosition.Trailing,
                    headlineContent = textContent(R.string.switch_profile),
                    supportingContent = textContent(R.string.settings_bottom_sheet__text_profile_switcher),
                )
            }
        }

        if (userProfileInfo != null) {
            divider(id = R.string.settings_profile_switcher__divider_current_profile)

            item(key = userProfileInfo.userHandle, contentType = ContentType.SingleGroupItem) {
                val textId = when {
                    isManagedProfile -> R.string.generic__label_work_profile
                    else -> R.string.generic__label_personal_profile
                }

                DefaultTwoLineIconClickableShapeListItem(
                    headlineContent = textContent(textId),
                    supportingContent = textContent(R.string.settings_profile_switcher__text_current_profile),
                    icon = Icons.Rounded.Person.iconPainter,
                    onClick = {}
                )
            }

            if (userProfileInfo.otherHandles.isNotEmpty()) {
                divider(id = R.string.settings_profile_switcher__divider_other_profiles)

                group(
                    list = userProfileInfo.otherHandles,
                    key = { (handle, _) -> handle }
                ) { (_, crossProfile), padding, shape ->
                    OtherProfiles(
                        shape = shape,
                        padding = padding,
                        crossProfile = crossProfile,
                        isManagedProfile = isManagedProfile,
                        startOther = startOther
                    )
                }
            }
        }
    }
}

@Composable
private fun OtherProfiles(
    shape: Shape,
    padding: PaddingValues,
    crossProfile: CrossProfile?,
    isManagedProfile: Boolean,
    startOther: (CrossProfile, Activity?) -> Unit,
) {
    val activity = LocalActivity.current
    if (crossProfile != null) {
        val textId = when {
            isManagedProfile -> R.string.generic__label_personal_profile
            else -> R.string.generic__label_work_profile
        }

        DefaultTwoLineIconClickableShapeListItem(
            shape = shape,
            padding = padding,
            headlineContent = textContent(textId),
            supportingContent = textContent(R.string.settings_profile_switcher__text_other_profile),
            icon = bitmap(crossProfile.drawable.toImageBitmap()),
            onClick = {
                startOther(crossProfile, activity)
            }
        )
    } else {
        DefaultTwoLineIconClickableShapeListItem(
            enabled = false,
            shape = shape,
            padding = padding,
            headlineContent = textContent(R.string.generic__label_unknown_profile),
            supportingContent = textContent(R.string.settings_profile_switcher__text_not_installed),
            icon = Icons.Rounded.Person.iconPainter,
            onClick = {}
        )
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Preview
@Composable
private fun ProfileSwitchingSettingsRoutePreview() {
    ProfileSwitchingSettingsRouteBase(
        status = ProfileStatus.NoProfiles,
        userProfileInfo = null
    )
}

@RequiresApi(Build.VERSION_CODES.P)
@Preview
@Composable
private fun ProfileSwitchingSettingsRoutePreview2() {
    ProfileSwitchingSettingsRouteBase(
        status = ProfileStatus.Available(listOf()),
        userProfileInfo = UserProfileInfo(
            1, emptyList()
        )
    )
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
private fun ProfileSwitchingSettingsRouteBase(status: ProfileStatus, userProfileInfo: UserProfileInfo?) {
    PreviewContainer {
        ProfileSwitchingSettingsRouteInternal(
            status = status,
            userProfileInfo = userProfileInfo,
            isManagedProfile = false,
            enabled = false,
            onEnable = {},
            onBackPressed = {},
            launchCrossProfileInteractSettings = {
            },
            startOther = { _, _ ->
            }
        )
    }
}
