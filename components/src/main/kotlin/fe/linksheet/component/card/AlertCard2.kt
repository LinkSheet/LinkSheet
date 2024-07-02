package fe.linksheet.component.card

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.component.PreviewThemeNew
import fe.linksheet.component.icon.FilledIcon
import fe.linksheet.component.list.base.ShapeListItemDefaults
import fe.linksheet.component.util.Default.Companion.text
import fe.linksheet.component.util.TextContent
import fe.linksheet.compose.util.PaddingValuesSides
import fe.linksheet.compose.util.atElevation
import fe.linksheet.compose.util.clickable
import fe.linksheet.compose.util.exclude


@Composable
fun ClickableAlertCard2(
    modifier: Modifier = AlertCardDefaults.MinHeight,
    colors: CardColors = CardDefaults.cardColors(),
    onClick: (() -> Unit)? = null,
    innerPadding: PaddingValues = AlertCardDefaults.InnerPadding,
    horizontalArrangement: Arrangement.Horizontal = AlertCardDefaults.HorizontalArrangement,
    imageVector: ImageVector,
    contentDescription: String?,
    headline: TextContent,
    subtitle: TextContent,
    content: @Composable (() -> Unit)? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShapeListItemDefaults.SingleShape)
            .clickable(onClick),
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
                imageVector = imageVector,
                iconSize = 20.dp,
                containerSize = 34.dp,
                contentDescription = contentDescription,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = containerColor,
//                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                    contentColor = Color.White
                    contentColor = contentColorFor(backgroundColor = containerColor)
                )
            )

            AlertCardContentLayout(
                title = {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.titleMedium,
                        content = headline.content
                    )
                },
                subtitle = {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                        content = subtitle.content
                    )
                }
            )
        }

        if (content != null) {
            Box(modifier = Modifier.padding(AlertCardDefaults.InnerPadding.exclude(PaddingValuesSides.Top))) {
                content()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ClickableAlertCard2Preview() {
    PreviewThemeNew {
        Column(modifier = Modifier.width(400.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ClickableAlertCard2(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                headline = text("Browser status"),
                subtitle = text("LinkSheet has been set as default browser!")
            )

            ClickableAlertCard2(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                headline = text("Shizuku integration"),
                subtitle = text("LinkSheet has detected at least one app known to be actually be a browser.")
            )
        }
    }
}
