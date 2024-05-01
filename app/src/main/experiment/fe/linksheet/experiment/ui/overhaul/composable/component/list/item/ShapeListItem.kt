package fe.linksheet.experiment.ui.overhaul.composable.component.list.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import fe.linksheet.experiment.ui.overhaul.composable.component.list.base.CustomListItem
import fe.linksheet.experiment.ui.overhaul.composable.util.OptionalContent
import fe.linksheet.experiment.ui.overhaul.composable.util.OptionalTextContent
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.experiment.ui.overhaul.composable.util.TextContent
import fe.linksheet.extension.compose.enabled


object ShapeListItemDefaults {
    // TODO: Use shape defaults or our own? Can we provide our own via LocalComposition or MaterialTheme?
    private val ShapeLarge = 20.dp //    ShapeDefaults.Large
    private val ShapeSmall = 4.dp

    val SingleShape = RoundedCornerShape(ShapeLarge)

    val TopShape = RoundedCornerShape(
        topStart = ShapeLarge,
        topEnd = ShapeLarge,
        bottomStart = ShapeSmall,
        bottomEnd = ShapeSmall
    )

    val MiddleShape = RoundedCornerShape(ShapeSmall)

    val BottomShape = RoundedCornerShape(
        topStart = ShapeSmall,
        topEnd = ShapeSmall,
        bottomEnd = ShapeLarge,
        bottomStart = ShapeLarge
    )

    val EmptyPadding = PaddingValues()
    val BaseModifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)
    val BaseContentModifier = Modifier.fillMaxHeight()

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        headlineColor: Color = contentColorFor(containerColor),
        supportingColor: Color = contentColorFor(containerColor),
    ): ListItemColors {
        return ListItemDefaults.colors(
            containerColor = containerColor,
            headlineColor = headlineColor,
            supportingColor = supportingColor
        )
    }
}

@Stable
enum class ContentPosition {
    Leading, Trailing;

    fun decide(position: ContentPosition, primary: OptionalContent, other: OptionalContent): OptionalContent {
        return if (position == this) primary else other
    }
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
        trailingContent = ContentPosition.Trailing.decide(position, primaryContent, otherContent)
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
        trailingContent = trailingContent
    )
}

@Composable
fun ShapeListItem(
    modifier: Modifier = ShapeListItemDefaults.BaseModifier,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = ShapeListItemDefaults.EmptyPadding,
    colors: ListItemColors = ShapeListItemDefaults.colors(),
    headlineContent: TextContent,
    overlineContent: OptionalTextContent = null,
    supportingContent: OptionalTextContent = null,
    leadingContent: OptionalContent = null,
    trailingContent: OptionalContent = null,
) {
    CustomListItem(
        modifier = Modifier.clip(shape).then(modifier).padding(padding),
        colors = colors,
        overlineContent = overlineContent?.content,
        headlineContent = headlineContent.content,
        leadingContent = leadingContent,
        supportingContent = supportingContent?.content,
        trailingContent = trailingContent
    )
}
