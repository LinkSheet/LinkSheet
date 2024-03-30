package fe.linksheet.experiment.ui.overhaul.composable.component.card

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Constraints

@Composable
fun AlertCardContentLayout(modifier: Modifier = Modifier, title: @Composable () -> Unit, subtitle: @Composable () -> Unit) {
    val measurePolicy = remember { AlertCardContentMeasurePolicy() }
    Layout(
        modifier = modifier,
        contents = listOf(title, subtitle),
        measurePolicy = measurePolicy,
    )
}

class AlertCardContentMeasurePolicy : MultiContentMeasurePolicy {
    override fun MeasureScope.measure(measurables: List<List<Measurable>>, constraints: Constraints): MeasureResult {
        val (title, subtitle) = measurables

        val titlePlaceable = title.first().measure(constraints)
        val subtitlePlaceable = subtitle.first().measure(constraints.copy(minHeight = titlePlaceable.height))

        val width = calculateWidth(
            titleWidth = titlePlaceable.width,
            subtitleWidth = subtitlePlaceable.width,
            constraints = constraints
        )

        val height = calculateHeight(
            titleHeight = titlePlaceable.height,
            subtitleHeight = subtitlePlaceable.height,
        )

        return layout(width, height) {
            var y = 0

            titlePlaceable.placeRelative(x = 0, y)
            y += titlePlaceable.height

            subtitlePlaceable.placeRelative(x = 0, y = y)
        }
    }
}

private fun calculateWidth(
    titleWidth: Int,
    subtitleWidth: Int,
    constraints: Constraints,
): Int {
    if (constraints.hasBoundedWidth) {
        return constraints.maxWidth
    }

    return maxOf(titleWidth, subtitleWidth)
}

private fun calculateHeight(
    titleHeight: Int,
    subtitleHeight: Int,
): Int {
    return titleHeight + subtitleHeight
}
