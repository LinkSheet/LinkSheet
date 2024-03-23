package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.preference

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import fe.android.preference.helper.Preference
import fe.android.preference.helper.compose.MutablePreferenceState
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.SwitchListItem


@Composable
fun PreferenceSwitchListItem(
    enabled: Boolean = true,
    preference: MutablePreferenceState<Boolean, Boolean, Preference.Default<Boolean>>,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    @StringRes headlineContentTextId: Int,
    @StringRes supportingContentTextId: Int? = null,
) {
    PreferenceSwitchListItem(
        enabled = enabled,
        preference = preference,
        shape = shape,
        padding = padding,
        headlineContentText = stringResource(id = headlineContentTextId),
        supportingContentText = supportingContentTextId?.let { stringResource(id = it) }
    )
}

@Composable
fun PreferenceSwitchListItem(
    enabled: Boolean = true,
    preference: MutablePreferenceState<Boolean, Boolean, Preference.Default<Boolean>>,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    headlineContentText: String,
    supportingContentText: String? = null,
) {
    SwitchListItem(
        enabled = enabled,
        shape = shape,
        padding = padding,
        checked = preference(),
        onCheckedChange = { preference(it) },
        headlineContentText = headlineContentText,
        supportingContentText = supportingContentText
    )
}
