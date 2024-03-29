package fe.linksheet.experiment.ui.overhaul.composable.component.card

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.extension.compose.clickable
import fe.linksheet.ui.HkGroteskFontFamily


@Composable
fun AlertCard(
    modifier: Modifier = Modifier.heightIn(min = 80.dp),
    colors: CardColors = CardDefaults.cardColors(),
    innerPadding: PaddingValues = PaddingValues(all = 16.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(12.dp),
    imageVector: ImageVector,
    @StringRes contentDescriptionId: Int?,
    @StringRes headlineId: Int,
    @StringRes subtitleId: Int,
) {
    ClickableAlertCard(
        modifier = modifier,
        colors = colors,
        innerPadding = innerPadding,
        horizontalArrangement = horizontalArrangement,
        imageVector = imageVector,
        contentDescriptionId = contentDescriptionId,
        headlineId = headlineId,
        subtitleId = subtitleId
    )
}

@Composable
fun ClickableAlertCard(
    modifier: Modifier = Modifier.heightIn(min = 80.dp),
    colors: CardColors = CardDefaults.cardColors(),
    onClick: (() -> Unit)? = null,
    innerPadding: PaddingValues = PaddingValues(all = 16.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(12.dp),
    imageVector: ImageVector,
    contentDescription: String?,
    headline: String,
    subtitle: String,
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
                .fillMaxHeight()
//                .then(modifier)
                .padding(innerPadding),
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
            )

//            ListItem(
//                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
//                headlineContent = { Text(text = headline,) },
//                supportingContent = { Text(text = subtitle,) }
//            )
//
            Column(verticalArrangement = Arrangement.Top) {
                Text(
//                    modifier = Modifier.weight(0.5f),
                    text = headline,
                    style = MaterialTheme.typography.titleMedium.merge(
                        TextStyle(
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Top,
                                trim = LineHeightStyle.Trim.Both
                            )
                        )
                    )
//                    style = MaterialTheme.typography.titleLarge.merge(
//                        TextStyle(
////                            lineHeight = 2.5.em,
//                            platformStyle = PlatformTextStyle(
//                                includeFontPadding = false
//                            ),
//                            lineHeightStyle = LineHeightStyle(
//                                alignment = LineHeightStyle.Alignment.Center,
//                                trim = LineHeightStyle.Trim.Both
//                            )
//                        )
//                    )
                )

                Text(
//                    modifier = Modifier.weight(0.5f),
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.merge(
                        TextStyle(
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Top,
                                trim = LineHeightStyle.Trim.Both
                            )
                        )
                    )
//                    style = MaterialTheme.typography.bodyMedium.merge(
//                        TextStyle(
////                            lineHeight = 2.5.em,
//                            platformStyle = PlatformTextStyle(
//                                includeFontPadding = false
//                            ),
//                            lineHeightStyle = LineHeightStyle(
//                                alignment = LineHeightStyle.Alignment.Center,
//                                trim = LineHeightStyle.Trim.Both
//                            )
//                        )
//                    )
                )
            }


//            Column {
//                Text(
//                    text = headline,
//                    style = MaterialTheme.typography.titleLarge
////                    style = MaterialTheme.typography.titleMedium
//                )
//
//                Text(
//                    text = subtitle,
////                    style = MaterialTheme.typography.bodyMedium
//
//                )
//            }
        }
    }
}


@Composable
fun ClickableAlertCard(
    modifier: Modifier = Modifier.heightIn(min = 80.dp),
    colors: CardColors = CardDefaults.cardColors(),
    onClick: (() -> Unit)? = null,
    innerPadding: PaddingValues = PaddingValues(all = 16.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(12.dp),
    imageVector: ImageVector,
    @StringRes contentDescriptionId: Int?,
    @StringRes headlineId: Int,
    @StringRes subtitleId: Int,
) {
    ClickableAlertCard(
        modifier = modifier,
        colors = colors,
        onClick = onClick,
        innerPadding = innerPadding,
        horizontalArrangement = horizontalArrangement,
        imageVector = imageVector,
        contentDescription = contentDescriptionId?.let { stringResource(id = it) },
        headline = stringResource(id = headlineId),
        subtitle = stringResource(id = subtitleId)
    )
}
