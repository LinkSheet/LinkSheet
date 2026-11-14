package app.linksheet.feature.clearurls.ui

import androidx.compose.runtime.Composable
import app.linksheet.feature.clearurls.viewmodel.ClearURLsSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ClearURLsSettings(
    onBackPressed: () -> Unit,
    viewModel: ClearURLsSettingsViewModel = koinViewModel(),
) {
//    val status by viewModel.status.collectAsStateWithLifecycle(ProfileStatus.Unsupported)
//    val userProfileInfo by viewModel.userProfileInfo.collectAsStateWithLifecycle(null)

//    ProfileSwitchingSettingsRouteInternal(
//        status = status,
//        userProfileInfo = userProfileInfo,
//        isManagedProfile = viewModel.checkIsManagedProfile(),
//        enabledPref = viewModel.enabled,
//        sendTargetPref = viewModel.sendTarget,
//        onBackPressed = onBackPressed,
//        launchCrossProfileInteractSettings = viewModel::launchCrossProfileInteractSettings,
//        startOther = viewModel::startOther,
//    )
}

//@RequiresApi(Build.VERSION_CODES.P)
//@Composable
//private fun ProfileSwitchingSettingsRouteInternal(
//    status: ProfileStatus,
//    userProfileInfo: UserProfileInfo?,
//    isManagedProfile: Boolean,
//    enabledPref: BooleanVmPref,
//    sendTargetPref: BooleanVmPref,
//    onBackPressed: () -> Unit,
//    launchCrossProfileInteractSettings: (Activity?) -> Unit,
//    startOther: (CrossProfile, Activity?) -> Unit,
//) {
//    val activity = LocalActivity.current
//
//    SaneScaffoldSettingsPage(
//        headline = stringResource(id = R.string.settings_profile_switcher__title_profile_switcher),
//        onBackPressed = onBackPressed
//    ) {
//        when (status) {
//            ProfileStatus.NoProfiles -> {
//                item(
//                    key = R.string.settings_profile_switcher__title_no_profile,
//                    contentType = ContentType.SingleGroupItem
//                ) {
//                    AlertCard(
//                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
//                        icon = Icons.Rounded.WarningAmber.iconPainter,
//                        iconOffset = IconOffset(y = (-1).dp),
//                        iconContentDescription = stringResource(id = R.string.settings_profile_switcher__title_no_profile),
//                        headline = textContent(R.string.settings_profile_switcher__title_no_profile),
//                        subtitle = textContent(R.string.settings_profile_switcher__text_no_profile),
//                        onClick = {
//                        }
//                    )
//                }
//            }
//            ProfileStatus.NotConnected -> {
//                item(
//                    key = R.string.settings_profile_switcher__title_enable_cross_profile,
//                    contentType = ContentType.SingleGroupItem
//                ) {
//                    AlertCard(
//                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
//                        icon = Icons.Rounded.WarningAmber.iconPainter,
//                        iconOffset = IconOffset(y = (-1).dp),
//                        iconContentDescription = stringResource(id = R.string.settings_profile_switcher__title_enable_cross_profile),
//                        headline = textContent(R.string.settings_profile_switcher__title_enable_cross_profile),
//                        subtitle = textContent(R.string.settings_profile_switcher__text_enable_cross_profile),
//                        onClick = {
//                            launchCrossProfileInteractSettings(activity)
//                        }
//                    )
//                }
//            }
//            else -> {}
//        }
//
//        if (status is ProfileStatus.Available) {
//            item(key = R.string.switch_profile, contentType = ContentType.SingleGroupItem) {
//                PreferenceSwitchListItem(
//                    statePreference = enabledPref,
//                    headlineContent = textContent(R.string.switch_profile),
//                    supportingContent = textContent(R.string.settings_bottom_sheet__text_profile_switcher),
//                )
//            }
//
//            divider(id = R.string.settings_profile_switcher__divider_options)
//
//            item(key = R.string.settings_profile_switcher__title_send_target, contentType = ContentType.SingleGroupItem) {
//                PreferenceSwitchListItem(
//                    statePreference = sendTargetPref,
//                    headlineContent = textContent(R.string.settings_profile_switcher__title_send_target),
//                    supportingContent = textContent(R.string.settings_profile_switcher__text_send_target),
//                )
//            }
//        }
//
//        if (userProfileInfo != null) {
//            divider(id = R.string.settings_profile_switcher__divider_current_profile)
//
//            item(key = userProfileInfo.userHandle, contentType = ContentType.SingleGroupItem) {
//                DefaultTwoLineIconClickableShapeListItem(
//                    headlineContent = textContent(id = when {
//                        isManagedProfile -> R.string.generic__label_work_profile
//                        else -> R.string.generic__label_personal_profile
//                    }),
//                    supportingContent = textContent(R.string.settings_profile_switcher__text_current_profile),
//                    icon = Icons.Rounded.Person.iconPainter,
//                    onClick = {}
//                )
//            }
//
//            if (userProfileInfo.otherHandles.isNotEmpty()) {
//                divider(id = R.string.settings_profile_switcher__divider_other_profiles)
//
//                group(
//                    list = userProfileInfo.otherHandles,
//                    key = { (handle, _) -> handle }
//                ) { (_, crossProfile), padding, shape ->
//                    OtherProfiles(
//                        shape = shape,
//                        padding = padding,
//                        crossProfile = crossProfile,
//                        isManagedProfile = isManagedProfile,
//                        startOther = startOther
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun OtherProfiles(
//    shape: Shape,
//    padding: PaddingValues,
//    crossProfile: CrossProfile?,
//    isManagedProfile: Boolean,
//    startOther: (CrossProfile, Activity?) -> Unit,
//) {
//    val activity = LocalActivity.current
//    if (crossProfile != null) {
//        val textId = when {
//            isManagedProfile -> R.string.generic__label_personal_profile
//            else -> R.string.generic__label_work_profile
//        }
//
//        DefaultTwoLineIconClickableShapeListItem(
//            shape = shape,
//            padding = padding,
//            headlineContent = textContent(textId),
//            supportingContent = textContent(R.string.settings_profile_switcher__text_other_profile),
//            icon = bitmap(crossProfile.drawable.toImageBitmap()),
//            onClick = {
//                startOther(crossProfile, activity)
//            }
//        )
//    } else {
//        DefaultTwoLineIconClickableShapeListItem(
//            enabled = false,
//            shape = shape,
//            padding = padding,
//            headlineContent = textContent(R.string.generic__label_unknown_profile),
//            supportingContent = textContent(R.string.settings_profile_switcher__text_not_installed),
//            icon = Icons.Rounded.Person.iconPainter,
//            onClick = {}
//        )
//    }
//}
//
//@RequiresApi(Build.VERSION_CODES.P)
//@Preview
//@Composable
//private fun ProfileSwitchingSettingsRoutePreview() {
//    ProfileSwitchingSettingsRouteBase(
//        status = ProfileStatus.NoProfiles,
//        userProfileInfo = null
//    )
//}
//
//@RequiresApi(Build.VERSION_CODES.P)
//@Preview
//@Composable
//private fun ProfileSwitchingSettingsRoutePreview2() {
//    ProfileSwitchingSettingsRouteBase(
//        status = ProfileStatus.Available(listOf()),
//        userProfileInfo = UserProfileInfo(
//            1, emptyList()
//        )
//    )
//}
//
//@RequiresApi(Build.VERSION_CODES.P)
//@Composable
//private fun ProfileSwitchingSettingsRouteBase(status: ProfileStatus, userProfileInfo: UserProfileInfo?) {
//    PreviewContainer {
//        ProfileSwitchingSettingsRouteInternal(
//            status = status,
//            userProfileInfo = userProfileInfo,
//            isManagedProfile = false,
//            enabledPref = fakeBooleanVM(true),
//            sendTargetPref = fakeBooleanVM(true),
//            onBackPressed = {},
//            launchCrossProfileInteractSettings = {
//            },
//            startOther = { _, _ ->
//            }
//        )
//    }
//}
