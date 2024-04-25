package fe.linksheet.experiment.ui.overhaul.composable.component.list.item

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.default.DefaultLeadingIconClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.TwoLineGroupValueProvider


@Stable
class RouteNavItem(
    val route: String,
    val icon: ImageVector,
    headlineId: Int,
    subtitleId: Int,
) : TwoLineGroupValueProvider(headlineId, subtitleId)

@Composable
fun RouteNavigateListItem(
    data: RouteNavItem,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    navigate: (String) -> Unit,
) {
    DefaultLeadingIconClickableShapeListItem(
        shape = shape,
        padding = padding,
        headlineId = data.headlineId,
        subtitleId = data.subtitleId,
        icon = data.icon,
        onClick = { navigate(data.route) }
    )
}
