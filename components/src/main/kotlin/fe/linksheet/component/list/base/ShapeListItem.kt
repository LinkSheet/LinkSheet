package fe.linksheet.component.list.base

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import fe.linksheet.component.util.OptionalContent
import fe.linksheet.component.util.OptionalTextContent
import fe.linksheet.component.util.TextContent


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

@Immutable
enum class ContentPosition {
    Leading, Trailing;

    @Stable
    fun decide(position: ContentPosition, primary: OptionalContent, other: OptionalContent): OptionalContent {
        return if (position == this) primary else other
    }
}



@Composable
fun ShapeListItem(
    modifier: Modifier = ShapeListItemDefaults.BaseModifier,
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
    CustomListItem(
        modifier = Modifier
            .clip(shape)
            .then(modifier)
            .padding(padding),
        colors = colors,
        overlineContent = overlineContent?.content,
        headlineContent = headlineContent.content,
        leadingContent = leadingContent,
        supportingContent = supportingContent?.content,
        trailingContent = trailingContent,
        containerHeight = containerHeight,
        padding = innerPadding,
        textOptions = textOptions
    )
}
