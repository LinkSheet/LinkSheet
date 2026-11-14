package app.linksheet.feature.clearurls.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import app.linksheet.compose.list.item.PreferenceDividedSwitchListItem
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.feature.clearurls.R
import app.linksheet.feature.clearurls.navigation.ClearURLsRoute
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.preference.helper.Preference
import fe.composekit.component.CommonDefaults
import fe.composekit.component.shape.CustomShapeDefaults
import fe.composekit.layout.column.SaneLazyColumnGroupScope
import fe.composekit.preference.ViewModelStatePreference
import fe.composekit.route.Route

fun SaneLazyColumnGroupScope.clearURLsListItem(
    statePreference: ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>,
    navigate: (Route) -> Unit,
) {
    item(key = R.string.use_clear_urls) { padding, shape ->
        ClearURLsListItem(
            shape = shape,
            padding = padding,
            statePreference = statePreference,
            navigate = navigate
        )
    }
}

@Composable
fun ClearURLsListItem(
    shape: Shape = CustomShapeDefaults.SingleShape,
    padding: PaddingValues = CommonDefaults.EmptyPadding,
    statePreference: ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>,
    navigate: (Route) -> Unit,
) {
    if (true) {
        PreferenceSwitchListItem(
            shape = shape,
            padding = padding,
            statePreference = statePreference,
            headlineContent = textContent(R.string.use_clear_urls),
            supportingContent = annotatedStringResource(R.string.use_clear_urls_explainer),

        )
    } else {
        PreferenceDividedSwitchListItem(
//        enabled = if (profileSwitcher.canQuickToggle()) EnabledContent.all else EnabledContent.Main.set,
            shape = shape,
            padding = padding,
            statePreference = statePreference,
            onContentClick = { navigate(ClearURLsRoute) },
            headlineContent = textContent(R.string.use_clear_urls),
            supportingContent = annotatedStringResource(R.string.use_clear_urls_explainer),
        )
    }
}
