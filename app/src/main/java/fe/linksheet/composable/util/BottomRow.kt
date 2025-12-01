package fe.linksheet.composable.util

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BottomRow(
    modifier: Modifier = Modifier,
    paddingEnd: Dp = 15.dp,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = paddingEnd)
            .height(50.dp),
        horizontalArrangement = Arrangement.End,
        content = content
    )
}
