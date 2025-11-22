package app.linksheet.feature.engine.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.linksheet.compose.preview.PreviewTheme


sealed class Comparison(override val label: String) : State {
    data object Equals : Comparison("equals")
    data object NotEquals : Comparison("does not equal")
    data object GreaterThan : Comparison("greater than")
    data object GreaterThanOrEqual : Comparison("greater than or equal")
    data object LessThan : Comparison("less than")
    data object LessThanOrEqual : Comparison("less than or equal")
}

private val comparison = listOf(
    Comparison.Equals,
    Comparison.NotEquals,
    Comparison.GreaterThan,
    Comparison.GreaterThanOrEqual,
    Comparison.LessThan,
    Comparison.LessThanOrEqual,
)

sealed class CompareValue(override val label: String) : State {
    data object OriginalUrl : CompareValue("original_url")
    data object ResultUrl : CompareValue("result_url")
}

private val compareValues = listOf(
    CompareValue.OriginalUrl,
    CompareValue.ResultUrl,
)

sealed class Operation(override val label: String) : State {
    data object GetComponent : Operation("get_component")
    data object GetParameter : Operation("get_parameter")
}

private val operations = listOf(
    Operation.GetComponent,
    Operation.GetParameter,
)

sealed class ComponentState(override val label: String) : State {
    data object Host : ComponentState("host")
    data object Path : ComponentState("path")
    data object Query : ComponentState("query")
}

private val componentStates = listOf(
    ComponentState.Host,
    ComponentState.Path,
    ComponentState.Query,
)

@Composable
private fun UrlCondition() {
    var selectedCompareValue by remember { mutableStateOf(compareValues[0]) }
    var selectedOperation by remember { mutableStateOf(operations[0]) }
    var selectedComparison by remember { mutableStateOf(comparison[0]) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
//        horizontalArrangement = Arrangement.spacedBy(8.dp),
//        verticalAlignment = Alignment.CenterVertically
    ) {
        StateDropdown(
            enabled = true,
            selected = selectedCompareValue,
            states = compareValues,
            onChange = { selectedCompareValue = it },
        )
        StateDropdown(
            enabled = true,
            selected = selectedOperation,
            states = operations,
            onChange = { selectedOperation = it },
        )
        if(selectedOperation is Operation.GetComponent) {
            var selectedComponent by remember { mutableStateOf(componentStates[0]) }
            StateDropdown(
                enabled = true,
                selected = selectedComponent,
                states = componentStates,
                onChange = { selectedComponent = it },
            )
        } else if(selectedOperation is Operation.GetParameter) {
            val valueState = rememberTextFieldState()
            TextField(
                state = valueState,
                lineLimits = TextFieldLineLimits.SingleLine,
                label = { Text("Parameter") },
            )
        }

        StateDropdown(
            enabled = true,
            selected = selectedComparison,
            states = comparison,
            onChange = { selectedComparison = it },
        )

        val valueState = rememberTextFieldState()
        TextField(
            modifier = Modifier.fillMaxWidth(),
            state = valueState,
            lineLimits = TextFieldLineLimits.SingleLine,
            label = { Text("Value") },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UrlConditionPreview() {
    PreviewTheme {
        UrlCondition()
    }
}
