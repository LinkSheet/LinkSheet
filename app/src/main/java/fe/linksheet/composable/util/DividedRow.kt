package fe.linksheet.composable.util

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fe.android.compose.extension.enabled
import fe.android.compose.extension.optionalClickable
import fe.android.compose.extension.thenIf


@Composable
fun DividedRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onLeftClick: (() -> Unit)? = null,
    paddingHorizontal: Dp = defaultHorizontalPadding,
    paddingVertical: Dp = defaultVerticalPadding,
    leftContent: @Composable ColumnScope.() -> Unit,
    rightContent: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = paddingHorizontal, vertical = paddingVertical)
            .enabled(enabled),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(defaultRoundedCornerShape)
                .thenIf(enabled) {
                    it.optionalClickable(onLeftClick)
                },
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
