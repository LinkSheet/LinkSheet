package fe.linksheet.composable.util

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import fe.android.compose.extension.enabled
import fe.android.compose.extension.thenIf

@Composable
fun NonClickableRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    paddingHorizontal: Dp = defaultHorizontalPadding,
    paddingVertical: Dp = defaultVerticalPadding,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) {
    ClickableRow(
        modifier = modifier,
        enabled = enabled,
        paddingHorizontal = paddingHorizontal,
        paddingVertical = paddingVertical,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content
    )
}

@Composable
fun ClickableRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    paddingHorizontal: Dp = defaultHorizontalPadding,
    paddingVertical: Dp = defaultVerticalPadding,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    ClickableRow(
        modifier = modifier,
        enabled = enabled,
        paddingHorizontal = paddingHorizontal,
        paddingVertical = paddingVertical,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        onClick = onClick,
        onLongClick = null,
        onDoubleClick = null,
        content = content
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClickableRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    paddingHorizontal: Dp = defaultHorizontalPadding,
    paddingVertical: Dp = defaultVerticalPadding,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(defaultRoundedCornerShape)
            .thenIf(enabled && onClick != null) {
                it.combinedClickable(
                    onClick = onClick!!,
                    onLongClick = onLongClick,
                    onDoubleClick = onDoubleClick
                )
            }
            .padding(horizontal = paddingHorizontal, vertical = paddingVertical)
            .enabled(enabled),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content
    )
}
