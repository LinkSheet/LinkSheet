package fe.linksheet.experiment.ui.overhaul.composable.component.list.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ListItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import fe.linksheet.experiment.ui.overhaul.composable.util.OptionalContent
import fe.linksheet.experiment.ui.overhaul.composable.util.OptionalTextContent
import fe.linksheet.experiment.ui.overhaul.composable.util.TextContent
import fe.linksheet.extension.compose.enabled

@Composable
fun ClickableShapeListItem(
    modifier: Modifier = ShapeListItemDefaults.BaseModifier,
    enabled: Boolean = true,
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
    ClickableShapeListItem(
        modifier = modifier,
        enabled = enabled,
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
fun ClickableShapeListItem(
    modifier: Modifier = ShapeListItemDefaults.BaseModifier,
    enabled: Boolean = true,
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
            .clickable(enabled = enabled, role = role, onClick = onClick)
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
