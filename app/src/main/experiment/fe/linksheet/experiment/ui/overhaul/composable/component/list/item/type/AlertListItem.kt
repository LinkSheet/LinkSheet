package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.ui.HkGroteskFontFamily

@Composable
fun AlertListItem(
    colors: ListItemColors = ShapeListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    imageVector: ImageVector,
    @StringRes headlineContentTextId: Int,
    @StringRes contentDescriptionTextId: Int?,
    @StringRes supportingContentTextId: Int,
) {
    ShapeListItem(
        colors = colors,
        leadingContent = {
            Icon(
                modifier = Modifier.fillMaxHeight(),
                imageVector = imageVector,
                contentDescription = contentDescriptionTextId?.let { stringResource(id = it) },
            )
        },
        headlineContent = {
            Text(
                text = stringResource(id = headlineContentTextId),
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold
            )
        },
        supportingContent = {
            Text(text = stringResource(id = supportingContentTextId))
        }
    )
}
