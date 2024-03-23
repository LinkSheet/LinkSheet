package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults


@Composable
fun RadioButtonListItem(
    enabled: Boolean = true,
    selected: Boolean,
    onClick: () -> Unit,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    headlineContentText: String,
    supportingContentText: String? = null,
) {
    ClickableShapeListItem(
        enabled = enabled,
        onClick = onClick,
        role = Role.RadioButton,
        shape = shape,
        padding = padding,
        headlineContent = {
            Text(text = headlineContentText)
        },
        supportingContent = supportingContentText?.let { { Text(text = it) } },
        trailingContent = {
            RadioButton(
                modifier = Modifier.fillMaxHeight(),
                enabled = enabled,
                selected = selected,
                onClick = onClick,
            )
        }
    )
}
