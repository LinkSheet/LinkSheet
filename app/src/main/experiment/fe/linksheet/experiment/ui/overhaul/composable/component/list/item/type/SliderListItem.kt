package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.experiment.ui.overhaul.composable.component.list.base.ShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.base.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.util.ComposableTextContent.Companion.content
import fe.linksheet.experiment.ui.overhaul.composable.util.Default.Companion.text
import fe.linksheet.experiment.ui.overhaul.composable.util.TextContent

@Composable
fun SliderListItem(
    enabled: Boolean = true,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    valueRangeStep: Float = 1f,
    onValueChange: (Float) -> Unit,
    valueFormatter: (Float) -> String = { it.toString() },
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = ShapeListItemDefaults.EmptyPadding,
    headlineContent: TextContent,
    supportingContent: TextContent? = null,
    overlineContent: TextContent? = null,
) {
    ShapeListItem(
        shape = shape,
        padding = padding,
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = content {
            Column {
                supportingContent?.content?.invoke()

                DefaultSliderListItem(
                    enabled = enabled,
                    value = value,
                    valueRange = valueRange,
                    valueRangeStep = valueRangeStep,
                    valueFormatter = valueFormatter,
                    onValueChange = onValueChange
                )
            }
        }
    )
}

@Composable
private fun DefaultSliderListItem(
    enabled: Boolean = true,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    valueRangeStep: Float = 1f,
    onValueChange: (Float) -> Unit,
    valueFormatter: (Float) -> String = { it.toString() },
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Slider(
            modifier = Modifier.weight(1.0f),
            enabled = enabled,
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = (((valueRange.endInclusive - valueRange.start) / valueRangeStep) - 1).toInt()
        )

        Text(text = valueFormatter(value))
    }
}

@Preview
@Composable
fun SliderListItemPreview() {
    SliderListItem(
        shape = ShapeListItemDefaults.SingleShape,
        padding = ShapeListItemDefaults.EmptyPadding,
        headlineContent = text("Preview headline"),
        supportingContent = text("Supprting\ncontent\nOkay?"),
        value = 1.0f,
        onValueChange = {},
    )
}
