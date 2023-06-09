package fe.linksheet.composable.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fe.linksheet.extension.clickable

@Composable
fun DividedRow(
    headline: String,
    subtitle: String? = null,
    subtitleBuilder: @Composable (() -> Unit)? = if (subtitle != null) {
        { SubtitleText(subtitle = subtitle) }
    } else null,
    onClick: () -> Unit,
    rightContent: @Composable RowScope.() -> Unit
) {
    DividedRow(
        onLeftClick = onClick,
        leftContent = {
            HeadlineText(headline = headline)
            subtitleBuilder?.invoke()
        },
        rightContent = rightContent
    )
}


@Composable
fun DividedRow(
    modifier: Modifier = Modifier,
    onLeftClick: (() -> Unit)? = null,
    paddingHorizontal: Dp = defaultHorizontalPadding,
    paddingVertical: Dp = defaultVerticalPadding,
    leftContent: @Composable ColumnScope.() -> Unit,
    rightContent: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = paddingHorizontal, vertical = paddingVertical),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(defaultRoundedCornerShape)
                .clickable(onLeftClick),
            content = leftContent
        )

        Divider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 8.dp)
                .width(1f.dp)
                .align(Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.tertiary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            content = rightContent
        )
    }
}