package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import fe.android.preference.helper.Preference
import fe.android.preference.helper.compose.MutablePreferenceState
import fe.linksheet.component.list.base.ContentPosition
import fe.linksheet.component.list.base.ShapeListItemDefaults
import fe.linksheet.component.list.item.type.SwitchListItem
import fe.linksheet.component.util.OptionalContent
import fe.linksheet.component.util.TextContent

@Composable
fun PreferenceSwitchListItem(
    enabled: Boolean = true,
    preference: MutablePreferenceState<Boolean, Boolean, Preference.Default<Boolean>>,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = ShapeListItemDefaults.EmptyPadding,
    headlineContent: TextContent,
    overlineContent: TextContent? = null,
    supportingContent: TextContent? = null,
    otherContent: OptionalContent = null,
) {
    SwitchListItem(
        enabled = enabled,
        shape = shape,
        padding = padding,
        position = ContentPosition.Trailing,
        checked = preference(),
        onCheckedChange = { preference(it) },
        overlineContent = overlineContent,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        otherContent = otherContent
    )
}
