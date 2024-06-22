package fe.linksheet.component.list.item

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Shape
import fe.linksheet.component.list.base.ShapeListItemDefaults
import fe.linksheet.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.linksheet.component.page.ListItemData
import fe.linksheet.component.util.IconType
import fe.linksheet.component.util.TextContent


@Stable
class RouteNavItem(
    val route: String,
    icon: IconType,
    headline: TextContent,
    subtitle: TextContent,
) : ListItemData<Any?>(icon, headline, subtitle)

@Composable
fun RouteNavigateListItem(
    data: RouteNavItem,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = ShapeListItemDefaults.EmptyPadding,
    navigate: (String) -> Unit,
) {
    DefaultTwoLineIconClickableShapeListItem(
        shape = shape,
        padding = padding,
        headlineContent = data.headlineContent,
        supportingContent = data.subtitleContent,
        icon = data.icon,
        onClick = { navigate(data.route) }
    )
}
