package fe.linksheet.activity.bottomsheet.content.failure

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fe.android.compose.content.OptionalContent
import fe.android.compose.extension.atElevation
import fe.android.compose.extension.optionalClickable
import fe.android.compose.icon.IconPainter
import fe.android.compose.padding.Top
import fe.android.compose.padding.exclude
import fe.android.compose.text.TextContent
import fe.composekit.component.card.AlertCardDefaults
import fe.composekit.component.icon.FilledIcon
import fe.composekit.component.icon.IconOffset
import fe.composekit.component.shape.CustomShapeDefaults

@Composable
fun FailureSheetLinkCard(
    modifier: Modifier = AlertCardDefaults.MinHeight,
    colors: CardColors = CardDefaults.cardColors(),
    shape: Shape = CustomShapeDefaults.SingleShape,
    onClick: (() -> Unit)? = null,
    innerPadding: PaddingValues = AlertCardDefaults.InnerPadding,
    horizontalArrangement: Arrangement.Horizontal = AlertCardDefaults.HorizontalArrangement,
    text: TextContent,
    icon: IconPainter,
    iconSize: Dp = AlertCardDefaults.IconSize,
    iconContainerSize: Dp = AlertCardDefaults.IconContainerSize,
    iconOffset: IconOffset? = null,
    iconContentDescription: String?,
    content: OptionalContent = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .optionalClickable(onClick = onClick),
        colors = colors
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
                .padding(innerPadding),
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val containerColor = colors.containerColor.atElevation(
                MaterialTheme.colorScheme.surfaceTint, 6.dp
            )

            FilledIcon(
                icon = icon,
                iconSize = iconSize,
                containerSize = iconContainerSize,
                iconOffset = iconOffset,
                contentDescription = iconContentDescription,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = containerColor,
                    contentColor = contentColorFor(backgroundColor = containerColor)
                )
            )

            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                content = text.content
            )
        }

        if (content != null) {
            Box(modifier = Modifier.padding(AlertCardDefaults.InnerPadding.exclude(Top))) {
                content()
            }
        }
    }
}
