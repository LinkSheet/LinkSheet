package app.linksheet.feature.shizuku.ui

import app.linksheet.feature.shizuku.R
import fe.android.preference.helper.Preference
import fe.composekit.layout.column.SaneLazyColumnGroupScope
import fe.composekit.preference.ViewModelStatePreference
import fe.composekit.route.Route

fun SaneLazyColumnGroupScope.shizukuListItem(
    statePreference: ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>,
    navigate: (Route) -> Unit,
) {
    item(key = R.string.settings_shizuku__title_shizuku) { padding, shape ->

    }
}

//@Composable
//fun ProfileSwitchingListItem(
//    shape: Shape = CustomShapeDefaults.SingleShape,
//    padding: PaddingValues = CommonDefaults.EmptyPadding,
//    statePreference: ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>,
//    profileSwitcher: ProfileSwitcher,
//    navigate: (Route) -> Unit,
//) {
//    PreferenceDividedSwitchListItem(
//        enabled = if (profileSwitcher.canQuickToggle()) EnabledContent.all else EnabledContent.Main.set,
//        shape = shape,
//        padding = padding,
//        statePreference = statePreference,
//        onContentClick = { navigate(ProfileRoute) },
//        headlineContent = textContent(R.string.switch_profile),
//        supportingContent = textContent(R.string.settings_bottom_sheet__text_profile_switcher),
//    )
//}
