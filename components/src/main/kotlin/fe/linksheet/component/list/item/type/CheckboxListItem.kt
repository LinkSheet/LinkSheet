package fe.linksheet.component.list.item.type

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import fe.linksheet.component.list.base.CustomListItemContainerHeight
import fe.linksheet.component.list.base.CustomListItemDefaults
import fe.linksheet.component.list.base.CustomListItemPadding
import fe.linksheet.component.list.base.CustomListItemTextOptions
import fe.linksheet.component.list.base.ClickableShapeListItem
import fe.linksheet.component.list.base.ContentPosition
import fe.linksheet.component.list.base.ShapeListItemDefaults
import fe.linksheet.component.util.OptionalContent
import fe.linksheet.component.util.TextContent

@Composable
fun CheckboxListItem(
    enabled: Boolean = true,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = ShapeListItemDefaults.EmptyPadding,
    colors: ListItemColors = ShapeListItemDefaults.colors(),
    containerHeight: CustomListItemContainerHeight = CustomListItemDefaults.containerHeight(),
    innerPadding: CustomListItemPadding = CustomListItemDefaults.padding(),
    textOptions: CustomListItemTextOptions = CustomListItemDefaults.textOptions(),
    position: ContentPosition,
    headlineContent: TextContent,
    overlineContent: TextContent? = null,
    supportingContent: TextContent? = null,
    otherContent: OptionalContent,
) {
    ClickableShapeListItem(
        enabled = enabled,
        onClick = { onCheckedChange(!checked) },
        role = Role.Checkbox,
        shape = shape,
        padding = padding,
        colors = colors,
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        position = position,
        primaryContent = {
            DefaultListItemCheckbox(enabled = enabled, checked = checked, onCheckedChange = onCheckedChange)
        },
        otherContent = otherContent,
        containerHeight = containerHeight,
        innerPadding = innerPadding,
        textOptions = textOptions
    )
}

@Composable
private fun DefaultListItemCheckbox(
    enabled: Boolean = true,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Checkbox(
        modifier = ShapeListItemDefaults.BaseContentModifier,
        enabled = enabled,
        checked = checked,
        onCheckedChange = onCheckedChange
    )
}
