package fe.linksheet.composable.page.settings.link

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import fe.linksheet.module.viewmodel.ClearUrlsProviderDialogState

@Composable
internal fun ClearUrlsProviderDialog(
    state: ClearUrlsProviderDialogState,
    onSearch: (String) -> Unit,
    onToggle: (String, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.clear_urls_providers_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.clear_urls_providers_description))
                OutlinedTextField(
                    value = state.search,
                    onValueChange = onSearch,
                    label = { Text(stringResource(R.string.search)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                when {
                    state.loading -> CircularProgressIndicator()
                    state.failed -> Text(stringResource(R.string.clear_urls_providers_load_error))
                    state.providers.isEmpty() -> Text(stringResource(R.string.clear_urls_providers_empty))
                    else -> LazyColumn(Modifier.weight(1f, fill = false).heightIn(max = 360.dp)) {
                        items(state.providers, key = { it }) { provider ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .toggleable(
                                        value = provider in state.selected,
                                        role = Role.Switch,
                                        onValueChange = { onToggle(provider, it) },
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Text(provider, modifier = Modifier.weight(1f))
                                Switch(checked = provider in state.selected, onCheckedChange = null)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) }
        },
    )
}

@Preview
@Composable
private fun ClearUrlsProviderDialogPreview() {
    PreviewTheme {
        ClearUrlsProviderDialog(
            state = ClearUrlsProviderDialogState(
                loading = false,
                providers = listOf("amazon", "instagram", "youtube"),
                selected = setOf("instagram"),
            ),
            onSearch = {},
            onToggle = { _, _ -> },
            onDismiss = {},
        )
    }
}
