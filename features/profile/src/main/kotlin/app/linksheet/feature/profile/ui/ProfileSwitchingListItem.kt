package app.linksheet.feature.profile.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import app.linksheet.compose.list.item.PreferenceDividedSwitchListItem
import app.linksheet.feature.profile.R
import app.linksheet.feature.profile.core.ProfileSwitcher
import app.linksheet.feature.profile.navigation.ProfileRoute
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.preference.helper.Preference
import fe.composekit.component.CommonDefaults
import fe.composekit.component.list.item.EnabledContent
import fe.composekit.component.shape.CustomShapeDefaults
import fe.composekit.layout.column.SaneLazyColumnGroupScope
import fe.composekit.preference.ViewModelStatePreference
import fe.composekit.route.Route

fun SaneLazyColumnGroupScope.profileSwitchingListItem(
    profileSwitcher: ProfileSwitcher,
    statePreference: ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>,
    navigate: (Route) -> Unit,
) {
    item(key = R.string.switch_profile) { padding, shape ->
        ProfileSwitchingListItem(
            shape = shape,
            padding = padding,
            statePreference = statePreference,
            profileSwitcher = profileSwitcher,
            navigate = navigate
        )
    }
}

@Composable
fun ProfileSwitchingListItem(
    shape: Shape = CustomShapeDefaults.SingleShape,
    padding: PaddingValues = CommonDefaults.EmptyPadding,
    statePreference: ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>,
    profileSwitcher: ProfileSwitcher,
    navigate: (Route) -> Unit,
) {
    PreferenceDividedSwitchListItem(
        enabled = if (profileSwitcher.canQuickToggle()) EnabledContent.all else EnabledContent.Main.set,
        shape = shape,
        padding = padding,
        statePreference = statePreference,
        onContentClick = { navigate(ProfileRoute) },
        headlineContent = textContent(R.string.switch_profile),
        supportingContent = textContent(R.string.settings_bottom_sheet__text_profile_switcher),
    )
}
