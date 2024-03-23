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
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.ui.HkGroteskFontFamily

@Composable
fun ClickableAlertListItem(
    enabled: Boolean = true,
    onClick: () -> Unit,
    role: Role? = Role.Button,
    colors: ListItemColors = ShapeListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    imageVector: ImageVector,
    @StringRes headlineContentTextId: Int,
    @StringRes contentDescriptionTextId: Int?,
    @StringRes supportingContentTextId: Int,
) {
    ClickableAlertListItem(
        enabled = enabled,
        onClick = onClick,
        role = role,
        colors = colors,
        imageVector = imageVector,
        headlineContentText = stringResource(id = headlineContentTextId),
        contentDescriptionText = contentDescriptionTextId?.let { stringResource(id = it) },
        supportingContentText = stringResource(id = supportingContentTextId)
    )
}

@Composable
fun ClickableAlertListItem(
    enabled: Boolean = true,
    onClick: () -> Unit,
    role: Role? = Role.Button,
    colors: ListItemColors = ShapeListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    imageVector: ImageVector,
    headlineContentText: String,
    contentDescriptionText: String?,
    supportingContentText: String,
) {
    ClickableShapeListItem(
        enabled = enabled,
        onClick = onClick,
        role = role,
        colors = colors,
        leadingContent = {
            Icon(
                modifier = Modifier.fillMaxHeight(),
                imageVector = imageVector,
                contentDescription = contentDescriptionText,
            )
        },
        headlineContent = {
            Text(
                text = headlineContentText,
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold
            )
        },
        supportingContent = {
            Text(text = supportingContentText)
        }
    )
}
