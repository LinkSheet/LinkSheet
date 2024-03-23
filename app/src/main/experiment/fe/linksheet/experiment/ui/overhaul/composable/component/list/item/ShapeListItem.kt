package fe.linksheet.experiment.ui.overhaul.composable.component.list.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import fe.linksheet.extension.compose.enabled


typealias OptionalContent = @Composable (() -> Unit)?

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

@Composable
fun ClickableShapeListItem(
    modifier: Modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min),
    enabled: Boolean = true,
    onClick: () -> Unit,
    role: Role? = null,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    colors: ListItemColors = ShapeListItemDefaults.colors(),
    headlineContent: @Composable () -> Unit,
    overlineContent: OptionalContent = null,
    supportingContent: OptionalContent = null,
    leadingContent: OptionalContent = null,
    trailingContent: OptionalContent = null,
) {
    ListItem(
        modifier = Modifier
            .clip(shape)
            .clickable(enabled = enabled, role = role, onClick = onClick)
            .enabled(enabled)
            .then(modifier)
            .padding(padding),
        colors = colors,
        overlineContent = overlineContent,
        headlineContent = headlineContent,
        leadingContent = leadingContent,
        supportingContent = supportingContent,
        trailingContent = trailingContent
    )
}

@Composable
fun ShapeListItem(
    modifier: Modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min),
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    colors: ListItemColors = ShapeListItemDefaults.colors(),
    headlineContent: @Composable () -> Unit,
    overlineContent: OptionalContent = null,
    supportingContent: OptionalContent = null,
    leadingContent: OptionalContent = null,
    trailingContent: OptionalContent = null,
) {
    ListItem(
        modifier = Modifier.clip(shape).then(modifier).padding(padding),
        colors = colors,
        overlineContent = overlineContent,
        headlineContent = headlineContent,
        leadingContent = leadingContent,
        supportingContent = supportingContent,
        trailingContent = trailingContent
    )
}
