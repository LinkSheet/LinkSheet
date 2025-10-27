@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package fe.linksheet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.linksheet.compose.preview.PreviewContainer
import fe.linksheet.R

@Composable
fun ConstUI() {
    var type by remember { mutableStateOf(ConstantType.String) }
    Column {
        Text(text = stringResource(R.string.settings_scenario__text_data_type))
        TypeSelector(type = type, onTypeChange = {
            type = it
        })

        Text(text = stringResource(R.string.settings_scenario__text_value))

        when (type) {
            ConstantType.String -> StringConstant()
            ConstantType.Boolean -> BooleanConstant()
            ConstantType.Integer -> StringConstant()
        }
    }
}

private object TypeSelector {
    val options = ConstantType.entries
    val unCheckedIcons = listOf(
        Icons.Outlined.ToggleOn,
        Icons.Outlined.TextFields,
        Icons.Outlined.Numbers,
    )
    val checkedIcons = listOf(
        Icons.Filled.ToggleOn,
        Icons.Filled.TextFields,
        Icons.Filled.Numbers,
    )
}

@Composable
private fun TypeSelector(type: ConstantType, onTypeChange: (ConstantType) -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        TypeSelector.options.forEachIndexed { index, item ->
            ToggleButton(
                checked = type == item,
                onCheckedChange = { onTypeChange(item) },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    TypeSelector.options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                modifier = Modifier.semantics { role = Role.RadioButton },
            ) {
                Icon(
                    imageVector = if (type == item) TypeSelector.checkedIcons[index] else TypeSelector.unCheckedIcons[index],
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.size(ToggleButtonDefaults.IconSpacing))
                Text(text = item.name)
            }
        }
    }
}


@Composable
private fun StringConstant() {
    TextField(
        state = rememberTextFieldState(),
        lineLimits = TextFieldLineLimits.SingleLine,
        label = { Text("Constant") },
    )
}

@Composable
private fun IntegerConstant() {

}

@Composable
private fun BooleanConstant() {
    var checked by remember { mutableStateOf(true) }

    Switch(
        checked = checked,
        onCheckedChange = {
            checked = it
        }
    )
}

enum class ConstantType {
    Boolean,
    String,
    Integer
}

@Preview(showBackground = true)
@Composable
private fun IfUIPreview() {
    PreviewContainer {
        ConstUI()
    }
}
