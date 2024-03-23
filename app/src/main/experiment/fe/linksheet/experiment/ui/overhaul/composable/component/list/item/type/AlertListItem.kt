package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.ui.HkGroteskFontFamily

@Composable
fun AlertListItem(
    modifier: Modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min),
    colors: ListItemColors = ShapeListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    imageVector: ImageVector,
    @StringRes headlineContentTextId: Int,
    @StringRes contentDescriptionTextId: Int?,
    @StringRes supportingContentTextId: Int,
) {
    ShapeListItem(
        modifier = modifier,
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
                fontWeight = FontWeight.SemiBold,
//                style = LocalTextStyle.current.merge(
//                    TextStyle(
//                        lineHeightStyle = LineHeightStyle(
//                            alignment = LineHeightStyle.Alignment.Center,
//                            trim = LineHeightStyle.Trim.FirstLineTop
//                        )
//                    )
//                )

            )
        },
        supportingContent = {
            Text(text = stringResource(id = supportingContentTextId))
        }
    )
}
