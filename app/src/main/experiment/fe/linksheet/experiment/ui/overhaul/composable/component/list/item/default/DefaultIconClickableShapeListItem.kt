package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.default

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ContentPosition
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.util.OptionalContent
import fe.linksheet.experiment.ui.overhaul.composable.component.util.TextContent
import fe.linksheet.experiment.ui.overhaul.composable.component.util.rememberOptionalContent

@Composable
fun DefaultIconClickableShapeListItem(
    enabled: Boolean = true,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = ShapeListItemDefaults.EmptyPadding,
    onClick: () -> Unit,
    position: ContentPosition = ContentPosition.Leading,
    headlineContent: TextContent,
    overlineContent: TextContent? = null,
    supportingContent: TextContent? = null,
    icon: ImageVector? = null,
    otherContent: OptionalContent = null,
) {
    val content: OptionalContent = rememberOptionalContent(icon) {
        DefaultListItemIcon(icon = it)
    }

    ClickableShapeListItem(
        enabled = enabled,
        shape = shape,
        padding = padding,
        onClick = onClick,
        role = Role.Button,
        overlineContent = overlineContent,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        position = position,
        primaryContent = content,
        otherContent = otherContent
    )
}

@Composable
private fun DefaultListItemIcon(
    icon: ImageVector,
    contentDescription: String? = null,
) {
    Icon(
        modifier = ShapeListItemDefaults.BaseContentModifier,
        imageVector = icon,
        contentDescription = contentDescription
    )
}
