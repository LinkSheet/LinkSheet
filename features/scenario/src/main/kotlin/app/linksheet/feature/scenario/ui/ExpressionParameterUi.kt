package app.linksheet.feature.scenario.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NamedInputChipParameter(
    name: String,
    label: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text12(text = name)
        ExpressionInputChip(label = label)
    }
}

@Composable
fun NamedInputParameter(
    name: String,
    parameter: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text12(text = name)
        parameter()
    }
}

@Preview(showBackground = true)
@Composable
private fun NamedInputChipParameterPreview() {
    NamedInputChipParameter(name = "action:") { Text12(text = "android.intent.action.VIEW") }
}
