package app.linksheet.compose.list.item

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Shape
import fe.android.compose.content.OptionalContent
import fe.android.compose.text.TextContent
import fe.android.preference.helper.Preference
import fe.android.preference.helper.compose.MutablePreferenceState
import fe.composekit.component.CommonDefaults
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.EnabledContent
import fe.composekit.component.list.item.EnabledContentSet
import fe.composekit.component.list.item.type.DividedSwitchListItem
import fe.composekit.component.shape.CustomShapeDefaults
import fe.composekit.preference.ViewModelStatePreference
import fe.composekit.preference.collectAsStateWithLifecycle

@Composable
fun PreferenceDividedSwitchListItem(
    enabled: EnabledContentSet = EnabledContent.all,
    preference: MutablePreferenceState<Boolean, Boolean, Preference.Default<Boolean>>,
    onContentClick: () -> Unit,
    shape: Shape = CustomShapeDefaults.SingleShape,
    padding: PaddingValues = CommonDefaults.EmptyPadding,
    headlineContent: TextContent,
    overlineContent: TextContent? = null,
    supportingContent: TextContent? = null,
    otherContent: OptionalContent = null,
) {
    DividedSwitchListItem(
        enabled = enabled,
        shape = shape,
        padding = padding,
        position = ContentPosition.Trailing,
        checked = preference(),
        onCheckedChange = { preference(it) },
        onContentClick = onContentClick,
        overlineContent = overlineContent,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        otherContent = otherContent
    )
}


@Composable
fun PreferenceDividedSwitchListItem(
    enabled: EnabledContentSet = EnabledContent.all,
    statePreference: ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>,
    onContentClick: () -> Unit,
    shape: Shape = CustomShapeDefaults.SingleShape,
    padding: PaddingValues = CommonDefaults.EmptyPadding,
    headlineContent: TextContent,
    overlineContent: TextContent? = null,
    supportingContent: TextContent? = null,
    otherContent: OptionalContent = null,
) {
    val preference by statePreference.collectAsStateWithLifecycle()

    DividedSwitchListItem(
        enabled = enabled,
        shape = shape,
        padding = padding,
        position = ContentPosition.Trailing,
        checked = preference,
        onCheckedChange = { statePreference.update(it) },
        onContentClick = onContentClick,
        overlineContent = overlineContent,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        otherContent = otherContent
    )
}
