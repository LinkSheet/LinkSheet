package fe.linksheet.component.card

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import fe.linksheet.compose.util.clickable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.component.PreviewThemeNew
import fe.linksheet.component.list.base.ShapeListItemDefaults


object AlertCardDefaults {
    val MinHeight = Modifier.heightIn(min = 90.dp)

    val Padding = 16.dp
    val InnerPadding = PaddingValues(all = Padding)

    val HorizontalArrangement = Arrangement.spacedBy(12.dp)
}

@Composable
fun AlertCard(
    modifier: Modifier = AlertCardDefaults.MinHeight,
    colors: CardColors = CardDefaults.cardColors(),
    innerPadding: PaddingValues = AlertCardDefaults.InnerPadding,
    horizontalArrangement: Arrangement.Horizontal = AlertCardDefaults.HorizontalArrangement,
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
    modifier: Modifier = AlertCardDefaults.MinHeight,
    colors: CardColors = CardDefaults.cardColors(),
    onClick: (() -> Unit)? = null,
    innerPadding: PaddingValues = AlertCardDefaults.InnerPadding,
    horizontalArrangement: Arrangement.Horizontal = AlertCardDefaults.HorizontalArrangement,
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

@Composable
fun ClickableAlertCard(
    modifier: Modifier = AlertCardDefaults.MinHeight,
    colors: CardColors = CardDefaults.cardColors(),
    onClick: (() -> Unit)? = null,
    innerPadding: PaddingValues = AlertCardDefaults.InnerPadding,
    horizontalArrangement: Arrangement.Horizontal = AlertCardDefaults.HorizontalArrangement,
    imageVector: ImageVector,
    contentDescription: String?,
    headline: String,
    subtitle: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth().clip(ShapeListItemDefaults.SingleShape).clickable(onClick),
        colors = colors
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().then(modifier).padding(innerPadding),
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
            )

            AlertCardContentLayout(
                title = { Text(text = headline, style = MaterialTheme.typography.titleMedium) },
                subtitle = { Text(text = subtitle, style = MaterialTheme.typography.bodyMedium) }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ClickableAlertCardPreview() {
    PreviewThemeNew {
        Column(modifier = Modifier.width(400.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ClickableAlertCard(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                headline = "Browser status",
                subtitle = "LinkSheet has been set as default browser!"
            )

            ClickableAlertCard(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                headline = "Shizuku integration",
                subtitle = "LinkSheet has detected at least one app known to be actually be a browser."
            )
        }
    }
}

