package fe.linksheet.composable.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RadioButtonRow(
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
    selected: Boolean,
    content: @Composable () -> Unit
) {
    ClickableRow(
        paddingHorizontal = 0.dp,
        paddingVertical = 5.dp,
        onClick = onClick,
        onLongClick = onLongClick,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.width(5.dp))
        content()
    }
}