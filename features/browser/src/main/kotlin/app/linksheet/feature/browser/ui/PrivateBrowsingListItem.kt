package app.linksheet.feature.browser.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import app.linksheet.compose.list.item.PreferenceDividedSwitchListItem
import app.linksheet.feature.browser.R
import app.linksheet.feature.browser.navigation.PrivateBrowsingRoute
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.preference.helper.Preference
import fe.composekit.component.CommonDefaults
import fe.composekit.component.shape.CustomShapeDefaults
import fe.composekit.preference.ViewModelStatePreference
import fe.composekit.route.Route

object PrivateBrowsingListItemDefaults {
    val Key = R.string.enable_request_private_browsing_button
}

@Composable
fun PrivateBrowsingListItem(
    shape: Shape = CustomShapeDefaults.SingleShape,
    padding: PaddingValues = CommonDefaults.EmptyPadding,
    statePreference: ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>,
    navigate: (Route) -> Unit,
) {
    PreferenceDividedSwitchListItem(
        shape = shape,
        padding = padding,
        statePreference = statePreference,
        onContentClick = { navigate(PrivateBrowsingRoute) },
        headlineContent = textContent(id = R.string.settings_private_browsing__title_private_browsing),
        supportingContent = textContent(
            id = R.string.enable_request_private_browsing_button_explainer
        )
    )
}
