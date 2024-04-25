package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.default

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotated

@Composable
fun DefaultLeadingIconClickableShapeListItem(
    enabled: Boolean = true,
    headlineText: AnnotatedString,
    subtitleText: AnnotatedString? = null,
    icon: ImageVector? = null,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    onClick: () -> Unit,
) {
    ClickableShapeListItem(
        enabled = enabled,
        shape = shape,
        padding = padding,
        onClick = onClick,
        role = Role.Button,
        leadingContent = icon?.let {
            {
                Icon(
                    modifier = Modifier.fillMaxHeight(),
                    imageVector = it,
                    contentDescription = headlineText.toString()
                )
            }
        },
        headlineContent = { Text(text = headlineText) },
        supportingContent = subtitleText?.let {
            {
                Text(text = it)
            }
        }
    )
}


@Composable
fun DefaultLeadingIconClickableShapeListItem(
    enabled: Boolean = true,
    @StringRes headlineId: Int,
    @StringRes subtitleId: Int? = null,
    icon: ImageVector? = null,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    onClick: () -> Unit,
) {
    ClickableShapeListItem(
        enabled = enabled,
        shape = shape,
        padding = padding,
        onClick = onClick,
        role = Role.Button,
        leadingContent = icon?.let {
            {
                Icon(
                    modifier = Modifier.fillMaxHeight(),
                    imageVector = it,
                    contentDescription = stringResource(id = headlineId)
                )
            }
        },
        headlineContent = { Text(text = stringResource(id = headlineId)) },
        supportingContent = subtitleId?.let {
            {
                Text(text = stringResource(id = it))
            }
        }
    )
}
