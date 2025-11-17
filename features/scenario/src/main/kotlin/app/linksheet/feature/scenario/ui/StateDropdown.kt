package app.linksheet.feature.scenario.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import fe.android.compose.extension.enabled


interface State {
    val label: String
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <S : State> StateDropdown(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    selected: S,
    states: List<S>,
    onChange: (S) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled)
                .fillMaxWidth()
                .enabled(enabled),
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (frontend in states) {
                StateDropdownItem(
                    frontend = frontend,
                    onClick = {
                        if (selected != frontend) {
                            onChange(frontend)
                        }
//                        if (selected.key != frontend.key) {
//                            onChange(frontend.key)
//                        }

                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun <S : State> StateDropdownItem(
    frontend: S,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(text = frontend.label) },
        onClick = onClick
    )
}
