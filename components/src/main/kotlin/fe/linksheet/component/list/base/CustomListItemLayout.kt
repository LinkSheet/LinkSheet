package fe.linksheet.component.list.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import kotlin.math.max

@Composable
fun CustomListItemLayout(
    containerHeight: CustomListItemContainerHeight,
    padding: CustomListItemPadding,
    leading: @Composable (() -> Unit)?,
    trailing: @Composable (() -> Unit)?,
    headline: @Composable () -> Unit,
    overline: @Composable (() -> Unit)?,
    supporting: @Composable (() -> Unit)?,
) {
    val measurePolicy = remember(containerHeight, padding) { CustomListItemMeasurePolicy(containerHeight, padding) }
    Layout(
        contents = listOf(
            headline,
            overline ?: {},
            supporting ?: {},
            leading ?: {},
            trailing ?: {},
        ),
        measurePolicy = measurePolicy,
    )
}

private class CustomListItemMeasurePolicy(
    val containerHeight: CustomListItemContainerHeight,
    val padding: CustomListItemPadding,
) : MultiContentMeasurePolicy {

    @JvmInline
    @Immutable
    private value class FirstOnly<T>(val items: List<List<T>>) {
        private fun atIdx(idx: Int) = items.getOrNull(idx)?.firstOrNull()
        operator fun component1() = atIdx(0)
        operator fun component2() = atIdx(1)
        operator fun component3() = atIdx(2)
        operator fun component4() = atIdx(3)
        operator fun component5() = atIdx(4)
    }

    private fun <T> List<List<T>>.firstOrNull() = FirstOnly(this)

    private fun minIntrinsicWidthOrZero(measurable: Measurable?, height: Int): Int {
        return measurable?.minIntrinsicWidth(height) ?: 0
    }

    private fun minIntrinsicHeightOrZero(measurable: Measurable?, width: Int): Int {
        return measurable?.minIntrinsicHeight(width) ?: 0
    }

    override fun MeasureScope.measure(
        measurables: List<List<Measurable>>,
        constraints: Constraints,
    ): MeasureResult {
        val (headline, overline, supporting, leading, trailing) = measurables.firstOrNull()

        var currentTotalWidth = 0

        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val horizontalPadding = padding.horizontal.roundToPx()

        // ListItem layout has a cycle in its dependencies which we use
        // intrinsic measurements to break:
        // 1. Intrinsic leading/trailing width
        // 2. Intrinsic supporting height
        // 3. Intrinsic vertical padding
        // 4. Actual leading/trailing measurement
        // 5. Actual supporting measurement
        // 6. Actual vertical padding
        val intrinsicLeadingWidth = minIntrinsicWidthOrZero(leading, constraints.maxHeight)
        val intrinsicTrailingWidth = minIntrinsicWidthOrZero(trailing, constraints.maxHeight)
        val intrinsicSupportingWidthConstraint = looseConstraints.maxWidth
            .subtractConstraintSafely(
                intrinsicLeadingWidth + intrinsicTrailingWidth + horizontalPadding
            )

        val intrinsicSupportingHeight = minIntrinsicHeightOrZero(supporting, intrinsicSupportingWidthConstraint)
        val intrinsicIsSupportingMultiline = isSupportingMultilineHeuristic(intrinsicSupportingHeight)

        val intrinsicListItemType = CustomListItemType(
            hasOverline = overline != null,
            hasSupporting = supporting != null,
            isSupportingMultiline = intrinsicIsSupportingMultiline,
        )

        val intrinsicVerticalPadding = (padding.verticalPadding(intrinsicListItemType) * 2).roundToPx()

        val paddedLooseConstraints = looseConstraints.offset(
            horizontal = -horizontalPadding,
            vertical = -intrinsicVerticalPadding,
        )

        val leadingPlaceable = leading?.measure(paddedLooseConstraints)
        currentTotalWidth += widthOrZero(leadingPlaceable)

        val trailingPlaceable = trailing?.measure(paddedLooseConstraints.offset(horizontal = -currentTotalWidth))
        currentTotalWidth += widthOrZero(trailingPlaceable)

        var currentTotalHeight = 0

        val headlinePlaceable = headline?.measure(paddedLooseConstraints.offset(horizontal = -currentTotalWidth))
        currentTotalHeight += heightOrZero(headlinePlaceable)

        val supportingPlaceable = supporting?.measure(
            paddedLooseConstraints.offset(
                horizontal = -currentTotalWidth,
                vertical = -currentTotalHeight
            )
        )

        currentTotalHeight += heightOrZero(supportingPlaceable)


        val overlinePlaceable = overline?.measure(
            paddedLooseConstraints.offset(
                horizontal = -currentTotalWidth,
                vertical = -currentTotalHeight
            )
        )

        val listItemType = CustomListItemType(
            hasOverline = overlinePlaceable != null,
            hasSupporting = supportingPlaceable != null,
            isSupportingMultiline = supportingPlaceable != null && (supportingPlaceable[FirstBaseline] != supportingPlaceable[LastBaseline]),
        )

        val topPadding = padding.verticalPadding(listItemType)
        val verticalPadding = topPadding * 2

        val width = calculateWidth(
            leadingWidth = widthOrZero(leadingPlaceable),
            trailingWidth = widthOrZero(trailingPlaceable),
            headlineWidth = widthOrZero(headlinePlaceable),
            overlineWidth = widthOrZero(overlinePlaceable),
            supportingWidth = widthOrZero(supportingPlaceable),
            horizontalPadding = horizontalPadding,
            constraints = constraints,
        )

        val height = calculateHeight(
            leadingHeight = heightOrZero(leadingPlaceable),
            trailingHeight = heightOrZero(trailingPlaceable),
            headlineHeight = heightOrZero(headlinePlaceable),
            overlineHeight = heightOrZero(overlinePlaceable),
            supportingHeight = heightOrZero(supportingPlaceable),
            defaultMinHeight = containerHeight.minHeight(listItemType).roundToPx(),
            verticalPadding = verticalPadding.roundToPx(),
            constraints = constraints,
        )

        return place(
            width = width,
            height = height,
            leadingPlaceable = leadingPlaceable,
            trailingPlaceable = trailingPlaceable,
            headlinePlaceable = headlinePlaceable,
            overlinePlaceable = overlinePlaceable,
            supportingPlaceable = supportingPlaceable,
            isThreeLine = listItemType == CustomListItemType.ThreeLine,
            padding = padding,
            topPadding = topPadding.roundToPx(),
        )
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurables: List<List<IntrinsicMeasurable>>,
        width: Int,
    ): Int = calculateIntrinsicHeight(measurables, width, IntrinsicMeasurable::maxIntrinsicHeight)

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurables: List<List<IntrinsicMeasurable>>,
        height: Int,
    ): Int = calculateIntrinsicWidth(measurables, height, IntrinsicMeasurable::maxIntrinsicWidth)

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurables: List<List<IntrinsicMeasurable>>,
        width: Int,
    ): Int = calculateIntrinsicHeight(measurables, width, IntrinsicMeasurable::minIntrinsicHeight)

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurables: List<List<IntrinsicMeasurable>>,
        height: Int,
    ): Int = calculateIntrinsicWidth(measurables, height, IntrinsicMeasurable::minIntrinsicWidth)

    private fun IntrinsicMeasureScope.calculateIntrinsicWidth(
        measurables: List<List<IntrinsicMeasurable>>,
        height: Int,
        intrinsicMeasure: IntrinsicMeasurable.(height: Int) -> Int,
    ): Int {
        val (
            headlineMeasurable,
            overlineMeasurable,
            supportingMeasurable,
            leadingMeasurable,
            trailingMeasurable,
        ) = FirstOnly(measurables)

        return calculateWidth(
            leadingWidth = leadingMeasurable?.intrinsicMeasure(height) ?: 0,
            trailingWidth = trailingMeasurable?.intrinsicMeasure(height) ?: 0,
            headlineWidth = headlineMeasurable?.intrinsicMeasure(height) ?: 0,
            overlineWidth = overlineMeasurable?.intrinsicMeasure(height) ?: 0,
            supportingWidth = supportingMeasurable?.intrinsicMeasure(height) ?: 0,
            horizontalPadding = padding.horizontal.roundToPx(),
            constraints = Constraints(),
        )
    }

    private fun IntrinsicMeasureScope.calculateIntrinsicHeight(
        measurables: List<List<IntrinsicMeasurable>>,
        width: Int,
        intrinsicMeasure: IntrinsicMeasurable.(width: Int) -> Int,
    ): Int {
        val (
            headline,
            overline,
            supporting,
            leading,
            trailing,
        ) = FirstOnly(measurables)

        var remainingWidth = width.subtractConstraintSafely(padding.horizontal.roundToPx())

        val leadingHeight = leading?.let {
            val height = it.intrinsicMeasure(remainingWidth)
            remainingWidth = remainingWidth.subtractConstraintSafely(it.maxIntrinsicWidth(Constraints.Infinity))
            height
        } ?: 0

        val trailingHeight = trailing?.let {
            val height = it.intrinsicMeasure(remainingWidth)
            remainingWidth = remainingWidth.subtractConstraintSafely(it.maxIntrinsicWidth(Constraints.Infinity))
            height
        } ?: 0

        val overlineHeight = overline?.intrinsicMeasure(remainingWidth) ?: 0
        val supportingHeight = supporting?.intrinsicMeasure(remainingWidth) ?: 0

        val isSupportingMultiline = isSupportingMultilineHeuristic(supportingHeight)
        val listItemType = CustomListItemType(
            hasOverline = overlineHeight > 0,
            hasSupporting = supportingHeight > 0,
            isSupportingMultiline = isSupportingMultiline,
        )

        return calculateHeight(
            leadingHeight = leadingHeight,
            trailingHeight = trailingHeight,
            headlineHeight = headline?.intrinsicMeasure(width) ?: 0,
            overlineHeight = overlineHeight,
            supportingHeight = supportingHeight,
            defaultMinHeight = containerHeight.minHeight(listItemType).roundToPx(),
            verticalPadding = (padding.verticalPadding(listItemType) * 2).roundToPx(),
            constraints = Constraints(),
        )
    }
}


private fun widthOrZero(placeable: Placeable?) = placeable?.width ?: 0
private fun heightOrZero(placeable: Placeable?) = placeable?.height ?: 0

private fun calculateWidth(
    leadingWidth: Int,
    trailingWidth: Int,
    headlineWidth: Int,
    overlineWidth: Int,
    supportingWidth: Int,
    horizontalPadding: Int,
    constraints: Constraints,
): Int {
    if (constraints.hasBoundedWidth) {
        return constraints.maxWidth
    }
    // Fallback behavior if width constraints are infinite
    val mainContentWidth = maxOf(headlineWidth, overlineWidth, supportingWidth)
    return horizontalPadding + leadingWidth + mainContentWidth + trailingWidth
}

private fun calculateHeight(
    leadingHeight: Int,
    trailingHeight: Int,
    headlineHeight: Int,
    overlineHeight: Int,
    supportingHeight: Int,
    defaultMinHeight: Int,
    verticalPadding: Int,
    constraints: Constraints,
): Int {
    val minHeight = max(constraints.minHeight, defaultMinHeight)

    val mainContentHeight = headlineHeight + overlineHeight + supportingHeight

    return max(
        minHeight,
        verticalPadding + maxOf(leadingHeight, mainContentHeight, trailingHeight)
    ).coerceAtMost(constraints.maxHeight)
}

private fun MeasureScope.place(
    width: Int,
    height: Int,
    leadingPlaceable: Placeable?,
    trailingPlaceable: Placeable?,
    headlinePlaceable: Placeable?,
    overlinePlaceable: Placeable?,
    supportingPlaceable: Placeable?,
    isThreeLine: Boolean,
    padding: CustomListItemPadding,
    topPadding: Int,
): MeasureResult {
    val startPadding = padding.start.roundToPx()
    val endPadding = padding.end.roundToPx()

    return layout(width, height) {
        leadingPlaceable?.placeRelative(
            x = startPadding,
            y = if (isThreeLine) topPadding else Alignment.CenterVertically.align(leadingPlaceable.height, height)
        )

        trailingPlaceable?.placeRelative(
            x = width - endPadding - trailingPlaceable.width,
            y = if (isThreeLine) topPadding else Alignment.CenterVertically.align(trailingPlaceable.height, height)
        )

        val mainContentX = startPadding + widthOrZero(leadingPlaceable)
        val mainContentY = if (isThreeLine) {
            topPadding
        } else {
            val totalHeight = heightOrZero(headlinePlaceable) +
                    heightOrZero(overlinePlaceable) +
                    heightOrZero(supportingPlaceable)

            Alignment.CenterVertically.align(totalHeight, height)
        }
        var currentY = mainContentY

        overlinePlaceable?.placeRelative(mainContentX, currentY)
        currentY += heightOrZero(overlinePlaceable)

        headlinePlaceable?.placeRelative(mainContentX, currentY)
        currentY += heightOrZero(headlinePlaceable)

        supportingPlaceable?.placeRelative(mainContentX, currentY)
    }
}

@JvmInline
@Immutable
value class CustomListItemType private constructor(
    private val lines: Int,
) : Comparable<CustomListItemType> {

    override operator fun compareTo(other: CustomListItemType) = lines.compareTo(other.lines)

    companion object {
        val OneLine = CustomListItemType(1)
        val TwoLine = CustomListItemType(2)
        val ThreeLine = CustomListItemType(3)

        internal operator fun invoke(
            hasOverline: Boolean,
            hasSupporting: Boolean,
            isSupportingMultiline: Boolean,
        ): CustomListItemType {
            return when {
                (hasOverline && hasSupporting) || isSupportingMultiline -> ThreeLine
                hasOverline || hasSupporting -> TwoLine
                else -> OneLine
            }
        }
    }
}


// In the actual layout phase, we can query supporting baselines,
// but for an intrinsic measurement pass, we have to estimate.
private fun Density.isSupportingMultilineHeuristic(
    estimatedSupportingHeight: Int,
): Boolean = estimatedSupportingHeight > 30.sp.roundToPx()

private fun Int.subtractConstraintSafely(n: Int): Int {
    if (this == Constraints.Infinity) {
        return this
    }
    return this - n
}

