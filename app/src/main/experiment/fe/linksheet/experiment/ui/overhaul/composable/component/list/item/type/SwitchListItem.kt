package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.OptionalContent
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults


@Composable
fun SwitchListItem(
    enabled: Boolean = true,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    headlineContentText: String,
    supportingContentText: String? = null,
) {
    SwitchListItem(
        enabled = enabled,
        checked = checked,
        onCheckedChange = onCheckedChange,
        shape = shape,
        padding = padding,
        headlineContent = { Text(text = headlineContentText) },
        supportingContent = supportingContentText?.let { { Text(text = it) } },
    )
}

@Composable
fun SwitchListItem(
    enabled: Boolean = true,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    headlineContent: @Composable () -> Unit,
    supportingContent: OptionalContent = null,
) {
    ClickableShapeListItem(
        enabled = enabled,
        onClick = { onCheckedChange(!checked) },
        role = Role.Switch,
        shape = shape,
        padding = padding,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        trailingContent = {
            Switch(
                modifier = Modifier.fillMaxHeight(),
                enabled = enabled,
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    )
}
