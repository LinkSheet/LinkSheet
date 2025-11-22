package app.linksheet.feature.engine.ui

import androidx.compose.foundation.layout.height
import androidx.compose.material3.AssistChip
import androidx.compose.material3.InputChip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpressionInputChip(
    label: @Composable () -> Unit,
) {
    InputChip(
        modifier = Modifier.height(32.dp),
        selected = true,
        label = label,
        onClick = {

        }
    )
}

@Composable
fun ExpressionAssistChip(text: String) {
    AssistChip(
        modifier = Modifier.height(32.dp),
        label = {
            Text12(text = text)
        },
        onClick = {}
    )
}
