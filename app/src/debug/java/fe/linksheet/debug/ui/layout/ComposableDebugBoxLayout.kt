package fe.linksheet.debug.ui.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.max

@Composable
fun ComposableDebugBoxLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val measurePolicy = remember { ComponentRendererLayoutMeasurePolicy() }
    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = measurePolicy,
    )
}

class ComponentRendererLayoutMeasurePolicy : MeasurePolicy {
    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        if (measurables.size != 1) error("Layout can only place a single child!")

        val contentConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val measurable = measurables.first()

        val placeable = measurable.measure(contentConstraints)

        val boxWidth = max(constraints.minWidth, placeable.width)
        val boxHeight = max(constraints.minHeight, placeable.height)

        return layout(boxWidth, boxHeight) {
            placeInBox(placeable, measurable, layoutDirection, boxWidth, boxHeight, Alignment.Center)
        }
    }
}

private class BoxChildDataNode(var alignment: Alignment) : ParentDataModifierNode, Modifier.Node() {
    override fun Density.modifyParentData(parentData: Any?) = this@BoxChildDataNode
}

private val Measurable.boxChildDataNode: BoxChildDataNode? get() = parentData as? BoxChildDataNode

private fun Placeable.PlacementScope.placeInBox(
    placeable: Placeable,
    measurable: Measurable,
    layoutDirection: LayoutDirection,
    boxWidth: Int,
    boxHeight: Int,
    alignment: Alignment,
) {
    val childAlignment = measurable.boxChildDataNode?.alignment ?: alignment
    val position = childAlignment.align(
        IntSize(placeable.width, placeable.height),
        IntSize(boxWidth, boxHeight),
        layoutDirection
    )

    placeable.place(position)
}
