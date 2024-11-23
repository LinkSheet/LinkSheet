package fe.linksheet.composable.page.settings.bottomsheet.profileswitcher

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import fe.android.compose.content.OptionalContent
import fe.android.compose.content.rememberOptionalContent
import fe.android.compose.extension.enabled
import fe.android.compose.icon.IconPainter
import fe.android.compose.text.TextContent
import fe.composekit.component.CommonDefaults
import fe.composekit.component.list.column.CustomListItemContainerHeight
import fe.composekit.component.list.column.CustomListItemDefaults
import fe.composekit.component.list.column.CustomListItemPadding
import fe.composekit.component.list.column.CustomListItemTextOptions
import fe.composekit.component.list.column.shape.ShapeListItem
import fe.composekit.component.list.column.shape.ShapeListItemDefaults
import fe.composekit.component.shape.CustomShapeDefaults

@Composable
fun ProfileListItem(
    modifier: Modifier = CommonDefaults.BaseModifier,
    enabled: Boolean = true,
    shape: Shape = CustomShapeDefaults.SingleShape,
    padding: PaddingValues = CommonDefaults.EmptyPadding,
    colors: ListItemColors = ShapeListItemDefaults.colors(),
    containerHeight: CustomListItemContainerHeight = CustomListItemDefaults.containerHeight(),
    innerPadding: CustomListItemPadding = CustomListItemDefaults.padding(),
    textOptions: CustomListItemTextOptions = CustomListItemDefaults.textOptions(),
    headlineContent: TextContent,
    overlineContent: TextContent? = null,
    supportingContent: TextContent? = null,
    icon: IconPainter? = null,
    otherContent: OptionalContent = null,
) {
    val content: OptionalContent = rememberOptionalContent(icon) {
        val painter = it.rememberPainter()

        Box(modifier = CommonDefaults.BaseContentModifier, contentAlignment = Alignment.Center) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painter,
                contentDescription = null
            )
        }
    }

    ShapeListItem(
        modifier = Modifier
            .enabled(enabled)
            .then(modifier),
        shape = shape,
        padding = padding,
        colors = colors,
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = content,
        trailingContent = null,
        containerHeight = containerHeight,
        innerPadding = innerPadding,
        textOptions = textOptions
    )
}
