package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.OptionalContent
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults


@Composable
fun TrailingRadioButtonListItem(
    enabled: Boolean = true,
    selected: Boolean,
    onClick: () -> Unit,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    headlineContentText: String,
    supportingContentText: String? = null,
) {
    RadioButtonListItem(
        enabled = enabled,
        selected = selected,
        onClick = onClick,
        location = RadioLocation.Trailing,
        shape = shape,
        padding = padding,
        headlineContentText = headlineContentText,
        supportingContentText = supportingContentText
    )
}

@Composable
fun LeadingRadioButtonListItem(
    modifier: Modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min),
    enabled: Boolean = true,
    selected: Boolean,
    onClick: () -> Unit,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    headlineContentText: String,
    supportingContentText: String? = null,
) {
    RadioButtonListItem(
        modifier = modifier,
        enabled = enabled,
        selected = selected,
        onClick = onClick,
        location = RadioLocation.Leading,
        shape = shape,
        padding = padding,
        headlineContentText = headlineContentText,
        supportingContentText = supportingContentText
    )
}

private enum class RadioLocation {
    Leading, Trailing
}

@Composable
private fun RadioButtonListItem(
    modifier: Modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min),
    enabled: Boolean = true,
    selected: Boolean,
    onClick: () -> Unit,
    location: RadioLocation,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = PaddingValues(),
    headlineContentText: String,
    supportingContentText: String? = null,
) {
    val radioButton: OptionalContent = remember {
        {
            RadioButton(
                modifier = Modifier.fillMaxHeight(),
                enabled = enabled,
                selected = selected,
                onClick = onClick,
            )
        }
    }

    ClickableShapeListItem(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        role = Role.RadioButton,
        shape = shape,
        padding = padding,
        headlineContent = {
            Text(text = headlineContentText)
        },
        supportingContent = supportingContentText?.let { { Text(text = it) } },
        leadingContent = if (location == RadioLocation.Leading) radioButton else null,
        trailingContent = if (location == RadioLocation.Trailing) radioButton else null
    )
}
