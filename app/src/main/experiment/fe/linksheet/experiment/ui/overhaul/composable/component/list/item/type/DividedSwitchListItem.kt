package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.android.preference.helper.Preference
import fe.android.preference.helper.compose.MutablePreferenceState
import fe.linksheet.component.list.base.ClickableShapeListItem
import fe.linksheet.component.list.base.ContentPosition
import fe.linksheet.component.list.base.ShapeListItemDefaults
import fe.linksheet.component.list.item.type.DividedSwitchListItem
import fe.linksheet.component.util.Default.Companion.text
import fe.linksheet.component.util.OptionalContent
import fe.linksheet.component.util.TextContent
import fe.linksheet.component.util.rememberOptionalContent


@Composable
fun PreferenceDividedSwitchListItem(
    enabled: Boolean = true,
    preference: MutablePreferenceState<Boolean, Boolean, Preference.Default<Boolean>>,
    onContentClick: () -> Unit,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = ShapeListItemDefaults.EmptyPadding,
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
