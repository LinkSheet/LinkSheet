package app.linksheet.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.composekit.component.PreviewThemeNew

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> ConnectedToggleButtonFlowRow(
    modifier: Modifier = Modifier,
    current: T,
    items: List<T>,
    onChange: (T) -> Unit,
    itemContent: @Composable RowScope.(T) -> Unit
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        for ((index, item) in items.withIndex()) {
            ToggleButton(
                modifier = Modifier.semantics { role = Role.RadioButton },
                checked = item == current,
                onCheckedChange = { onChange(item) },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    items.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
            ) {
                itemContent(item)
            }
        }
    }
}

private enum class PreviewDummy {
    Auto, Manual
}

@Preview(showBackground = true)
@Composable
private fun ConnectedToggleButtonFlowRowPreview() {
    PreviewThemeNew {
        ConnectedToggleButtonFlowRow(
            items = listOf(PreviewDummy.Auto, PreviewDummy.Manual),
            current = PreviewDummy.Auto,
            onChange = {

            },
            itemContent = {
                Text(text = it.toString())
            }
        )
    }
}
