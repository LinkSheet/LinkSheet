package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import fe.android.compose.content.OptionalContent
import fe.android.compose.text.TextContent
import fe.android.preference.helper.Preference
import fe.android.preference.helper.compose.MutablePreferenceState
import fe.composekit.component.CommonDefaults
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.DividedSwitchListItem
import fe.composekit.component.shape.CustomShapeDefaults


@Composable
fun PreferenceDividedSwitchListItem(
    enabled: Boolean = true,
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
