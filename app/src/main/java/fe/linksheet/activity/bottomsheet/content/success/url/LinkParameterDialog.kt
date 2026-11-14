package fe.linksheet.activity.bottomsheet.content.success.url

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.ui.PreviewTheme
import fe.linksheet.module.viewmodel.LinkParameterSelection

@Composable
internal fun LinkParameterDialog(
    state: LinkParameterSelection,
    onToggle: (Int, Boolean) -> Unit,
    onSelectAll: (Boolean) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.link_parameters_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.link_parameters_description))
                if (state.parameters.isEmpty()) {
                    Text(stringResource(R.string.link_parameters_empty))
                } else {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { onSelectAll(true) }) {
                            Text(stringResource(R.string.link_parameters_select_all))
                        }
                        TextButton(onClick = { onSelectAll(false) }) {
                            Text(stringResource(R.string.link_parameters_deselect_all))
                        }
                    }
                    LazyColumn(Modifier.weight(1f, fill = false).heightIn(max = 360.dp)) {
                        itemsIndexed(state.parameters, key = { index, _ -> index }) { index, parameter ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .toggleable(
                                        value = index in state.selected,
                                        role = Role.Checkbox,
                                        onValueChange = { onToggle(index, it) },
                                    )
                                    .heightIn(min = 48.dp)
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Checkbox(checked = index in state.selected, onCheckedChange = null)
                                Text(parameter, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onApply) { Text(stringResource(R.string.link_parameters_apply)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        },
    )
}

@Preview
@Composable
private fun LinkParameterDialogPreview() {
    PreviewTheme {
        LinkParameterDialog(
            state = LinkParameterSelection.from("https://www.instagram.com/p/DcejiwAiFRd/?img_index=4&igsi=MTNpb2F4aHYzN2VyOA==")
                .select(1, false),
            onToggle = { _, _ -> },
            onSelectAll = {},
            onApply = {},
            onDismiss = {},
        )
    }
}
