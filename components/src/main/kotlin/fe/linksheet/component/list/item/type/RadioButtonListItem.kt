package fe.linksheet.component.list.item.type

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fe.linksheet.component.list.base.*
import fe.linksheet.component.util.OptionalContent
import fe.linksheet.component.util.TextContent

object RadioButtonListItemDefaults {
    val Width = 24.dp
}

@Composable
fun RadioButtonListItem(
    modifier: Modifier = ShapeListItemDefaults.BaseModifier,
    enabled: Boolean = true,
    width: Dp = RadioButtonListItemDefaults.Width,
    selected: Boolean,
    onSelect: () -> Unit,
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
    SelectableShapeListItem(
        modifier = modifier,
        enabled = enabled,
        selected = selected,
        onClick = onSelect,
        role = Role.RadioButton,
        shape = shape,
        padding = padding,
        colors = colors,
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        position = position,
        primaryContent = {
            DefaultListItemRadioButton(enabled = enabled, width = width, selected = selected, onSelect = onSelect)
        },
        otherContent = otherContent,
        containerHeight = containerHeight,
        innerPadding = innerPadding,
        textOptions = textOptions
    )
}

@Composable
private fun DefaultListItemRadioButton(
    enabled: Boolean = true,
    width: Dp = RadioButtonListItemDefaults.Width,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides width) {
        RadioButton(
            modifier = ShapeListItemDefaults.BaseContentModifier.width(width),
            enabled = enabled,
            selected = selected,
            onClick = onSelect,
        )
    }
}
