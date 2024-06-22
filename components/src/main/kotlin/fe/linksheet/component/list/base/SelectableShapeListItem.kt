package fe.linksheet.component.list.base

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ListItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import fe.linksheet.component.util.OptionalContent
import fe.linksheet.component.util.OptionalTextContent
import fe.linksheet.component.util.TextContent
import fe.linksheet.compose.util.enabled

@Composable
fun SelectableShapeListItem(
    modifier: Modifier = ShapeListItemDefaults.BaseModifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    onClick: () -> Unit,
    role: Role? = null,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = ShapeListItemDefaults.EmptyPadding,
    colors: ListItemColors = ShapeListItemDefaults.colors(),
    containerHeight: CustomListItemContainerHeight = CustomListItemDefaults.containerHeight(),
    innerPadding: CustomListItemPadding = CustomListItemDefaults.padding(),
    textOptions: CustomListItemTextOptions = CustomListItemDefaults.textOptions(),
    position: ContentPosition,
    headlineContent: TextContent,
    overlineContent: OptionalTextContent = null,
    supportingContent: OptionalTextContent = null,
    primaryContent: OptionalContent = null,
    otherContent: OptionalContent = null,
) {
    SelectableShapeListItem(
        modifier = modifier,
        enabled = enabled,
        selected = selected,
        onClick = onClick,
        role = role,
        shape = shape,
        padding = padding,
        colors = colors,
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = ContentPosition.Leading.decide(position, primaryContent, otherContent),
        trailingContent = ContentPosition.Trailing.decide(position, primaryContent, otherContent),
        containerHeight = containerHeight,
        innerPadding = innerPadding,
        textOptions = textOptions
    )
}

@Composable
fun SelectableShapeListItem(
    modifier: Modifier = ShapeListItemDefaults.BaseModifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    onClick: () -> Unit,
    role: Role? = null,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = ShapeListItemDefaults.EmptyPadding,
    colors: ListItemColors = ShapeListItemDefaults.colors(),
    containerHeight: CustomListItemContainerHeight = CustomListItemDefaults.containerHeight(),
    innerPadding: CustomListItemPadding = CustomListItemDefaults.padding(),
    textOptions: CustomListItemTextOptions = CustomListItemDefaults.textOptions(),
    headlineContent: TextContent,
    overlineContent: OptionalTextContent = null,
    supportingContent: OptionalTextContent = null,
    leadingContent: OptionalContent = null,
    trailingContent: OptionalContent = null,
) {
    ShapeListItem(
        modifier = Modifier
            .selectable(enabled = enabled, selected = selected, role = role, onClick = onClick)
            .enabled(enabled)
            .then(modifier),
        shape = shape,
        padding = padding,
        colors = colors,
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        containerHeight = containerHeight,
        innerPadding = innerPadding,
        textOptions = textOptions
    )
}
