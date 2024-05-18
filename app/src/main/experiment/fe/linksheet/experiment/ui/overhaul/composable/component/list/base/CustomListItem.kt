package fe.linksheet.experiment.ui.overhaul.composable.component.list.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fe.linksheet.experiment.ui.overhaul.composable.util.ProvideContentColorTextStyle
import fe.linksheet.experiment.ui.overhaul.composable.util.TextOptions


object CustomListItemDefaults {
    private val ContainerHeightOneLine = 56.0.dp
    private val ContainerHeightTwoLine = 76.0.dp
    private val ContainerHeightThreeLine = 88.0.dp

    private val VerticalPadding = 8.dp
    private val ThreeLineVerticalPadding = 12.dp

    private val StartPadding = 16.dp
    private val EndPadding = 16.dp

    private val LeadingContentEndPadding = 16.dp
    private val TrailingContentStartPadding = 16.dp

    val HeadlineTextOptions = TextOptions(maxLines = 1, overflow = TextOverflow.Ellipsis)

    @Stable
    fun containerHeight(
        oneLine: Dp = ContainerHeightOneLine,
        twoLine: Dp = ContainerHeightTwoLine,
        threeLine: Dp = ContainerHeightThreeLine
    ): CustomListItemContainerHeight {
        return CustomListItemContainerHeight(oneLine, twoLine, threeLine)
    }

    @Stable
    fun padding(
        vertical: Dp = VerticalPadding,
        threeLineVertical: Dp = ThreeLineVerticalPadding,
        start: Dp = StartPadding,
        end: Dp = EndPadding,
        leadingContentEnd: Dp = LeadingContentEndPadding,
        trailingContentStart: Dp = TrailingContentStartPadding
    ): CustomListItemPadding {
        return CustomListItemPadding(vertical, threeLineVertical, start, end, leadingContentEnd, trailingContentStart)
    }

    @Stable
    fun textOptions(
        overline: TextOptions? = null,
        headline: TextOptions? = HeadlineTextOptions,
        supporting: TextOptions? = null,
    ): CustomListItemTextOptions {
        return CustomListItemTextOptions(overline, headline, supporting)
    }
}

@Immutable
data class CustomListItemContainerHeight(
    val oneLine: Dp,
    val twoLine: Dp,
    val threeLine: Dp,
) {
    @Stable
    fun minHeight(listItemType: CustomListItemType): Dp {
        return when (listItemType) {
            CustomListItemType.OneLine -> oneLine
            CustomListItemType.TwoLine -> twoLine
            else -> threeLine
        }
    }
}

@Immutable
data class CustomListItemPadding(
    private val vertical: Dp,
    private val threeLineVertical: Dp,
    val start: Dp,
    val end: Dp,
    val leadingContentEnd: Dp,
    val trailingContentStart: Dp
) {
    val horizontal = start + end

    @Stable
    fun verticalPadding(listItemType: CustomListItemType): Dp {
        return when (listItemType) {
            CustomListItemType.ThreeLine -> threeLineVertical
            else -> vertical
        }
    }
}

@Immutable
data class CustomListItemTextOptions(
    val overline: TextOptions?,
    val headline: TextOptions?,
    val supporting: TextOptions?
)

@Composable
fun CustomListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    containerHeight: CustomListItemContainerHeight = CustomListItemDefaults.containerHeight(),
    padding: CustomListItemPadding = CustomListItemDefaults.padding(),
    textOptions: CustomListItemTextOptions = CustomListItemDefaults.textOptions()
) {
    val decoratedHeadlineContent: @Composable () -> Unit = {
        ProvideContentColorTextStyle(
            colors.headlineColor(enabled = true),
            MaterialTheme.typography.bodyLarge,
            textOptions.headline,
            content = headlineContent
        )
    }

    val decoratedSupportingContent: @Composable (() -> Unit)? = supportingContent?.let {
        {
            ProvideContentColorTextStyle(
                colors.supportingTextColor,
                MaterialTheme.typography.bodyMedium,
                textOptions.supporting,
                content = it
            )
        }
    }

    val decoratedOverlineContent: @Composable (() -> Unit)? = overlineContent?.let {
        {
            ProvideContentColorTextStyle(
                colors.overlineColor,
                MaterialTheme.typography.labelSmall,
                textOptions.overline,
                content = it
            )
        }
    }

    val decoratedLeadingContent: @Composable (() -> Unit)? = leadingContent?.let {
        {
            Box(modifier = Modifier.padding(end = padding.leadingContentEnd)) {
                CompositionLocalProvider(
                    LocalContentColor provides colors.leadingIconColor(enabled = true),
                    content = it
                )
            }
        }
    }

    val decoratedTrailingContent: @Composable (() -> Unit)? = trailingContent?.let {
        {
            Box(modifier = Modifier.padding(start = padding.trailingContentStart)) {
                ProvideContentColorTextStyle(
                    colors.trailingIconColor(enabled = true),
                    MaterialTheme.typography.labelSmall,
                    content = it
                )
            }
        }
    }

    Surface(
        modifier = Modifier
            .semantics(mergeDescendants = true) {}
            .then(modifier),
        shape = ListItemDefaults.shape,
        color = colors.containerColor,
        contentColor = colors.headlineColor(enabled = true),
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
    ) {
        CustomListItemLayout(
            containerHeight = containerHeight,
            padding = padding,
            headline = decoratedHeadlineContent,
            overline = decoratedOverlineContent,
            supporting = decoratedSupportingContent,
            leading = decoratedLeadingContent,
            trailing = decoratedTrailingContent,
        )
    }
}

@Stable
private fun ListItemColors.headlineColor(enabled: Boolean): Color {
    return if (enabled) headlineColor else disabledHeadlineColor
}

@Stable
private fun ListItemColors.leadingIconColor(enabled: Boolean): Color {
    return if (enabled) leadingIconColor else disabledLeadingIconColor
}

@Stable
private fun ListItemColors.trailingIconColor(enabled: Boolean): Color {
    return if (enabled) trailingIconColor else disabledTrailingIconColor
}
