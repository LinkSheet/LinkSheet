package fe.linksheet.component.list.item.type

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import fe.linksheet.component.list.base.ClickableShapeListItem
import fe.linksheet.component.list.base.ContentPosition
import fe.linksheet.component.list.base.ShapeListItemDefaults
import fe.linksheet.component.util.OptionalContent
import fe.linksheet.component.util.TextContent
import fe.linksheet.component.util.rememberOptionalContent

@Composable
fun SwitchListItem(
    enabled: Boolean = true,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = ShapeListItemDefaults.EmptyPadding,
    position: ContentPosition,
    headlineContent: TextContent,
    overlineContent: TextContent? = null,
    supportingContent: TextContent? = null,
    otherContent: OptionalContent = null,
) {
    ClickableShapeListItem(
        enabled = enabled,
        onClick = { onCheckedChange(!checked) },
        role = Role.Switch,
        shape = shape,
        padding = padding,
        position = position,
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        primaryContent = {
            DefaultListItemSwitch(
                enabled = enabled,
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        otherContent = otherContent
    )
}

@Composable
private fun DefaultListItemSwitch(
    enabled: Boolean = true,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val thumbContent = rememberOptionalContent(checked) {
        Icon(
            modifier = Modifier.size(SwitchDefaults.IconSize),
            imageVector = Icons.Filled.Check,
            contentDescription = null,
        )
    }

    Switch(
        modifier = ShapeListItemDefaults.BaseContentModifier,
        enabled = enabled,
        checked = checked,
        thumbContent = thumbContent,
        onCheckedChange = onCheckedChange
    )
}
