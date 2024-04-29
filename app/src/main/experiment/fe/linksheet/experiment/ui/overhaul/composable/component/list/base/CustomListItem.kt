package fe.linksheet.experiment.ui.overhaul.composable.component.list.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fe.linksheet.experiment.ui.overhaul.composable.component.util.ProvideContentColorTextStyle
import fe.linksheet.experiment.ui.overhaul.composable.component.util.TextOptions


object CustomListItemDefaults {
    val ContainerHeightOneLine = 56.0.dp
    val ContainerHeightTwoLine = 76.0.dp
    val ContainerHeightThreeLine = 88.0.dp

    val VerticalPadding = 8.dp
    val ThreeLineVerticalPadding = 12.dp

    val StartPadding = 16.dp
    val EndPadding = 16.dp

    val LeadingContentEndPadding = 16.dp
    val TrailingContentStartPadding = 16.dp

    val headlineTextOptions = TextOptions(maxLines = 1, overflow = TextOverflow.Ellipsis)
}

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
) {
    val decoratedHeadlineContent: @Composable () -> Unit = {
        ProvideContentColorTextStyle(
            colors.headlineColor(enabled = true),
            MaterialTheme.typography.bodyLarge,
            CustomListItemDefaults.headlineTextOptions,
            content = headlineContent
        )
    }
    val decoratedSupportingContent: @Composable (() -> Unit)? = supportingContent?.let {
        {
            ProvideContentColorTextStyle(
                colors.supportingTextColor,
                MaterialTheme.typography.bodyMedium,
                content = it
            )
        }
    }
    val decoratedOverlineContent: @Composable (() -> Unit)? = overlineContent?.let {
        {
            ProvideContentColorTextStyle(
                colors.overlineColor,
                MaterialTheme.typography.labelSmall,
                content = it
            )
        }
    }
    val decoratedLeadingContent: @Composable (() -> Unit)? = leadingContent?.let {
        {
            Box(modifier = Modifier.padding(end = CustomListItemDefaults.LeadingContentEndPadding)) {
                CompositionLocalProvider(
                    LocalContentColor provides colors.leadingIconColor(enabled = true),
                    it
                )
            }
        }
    }
    val decoratedTrailingContent: @Composable (() -> Unit)? = trailingContent?.let {
        {
            Box(modifier = Modifier.padding(start = CustomListItemDefaults.TrailingContentStartPadding)) {
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
            startPadding = CustomListItemDefaults.StartPadding,
            endPadding = CustomListItemDefaults.EndPadding,
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
