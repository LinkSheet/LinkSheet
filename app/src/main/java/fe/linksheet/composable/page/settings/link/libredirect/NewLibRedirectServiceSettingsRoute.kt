package fe.linksheet.composable.page.settings.link.libredirect

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fe.android.compose.extension.enabled
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.toEnabledContentSet
import fe.composekit.component.list.item.type.RadioButtonListItem
import fe.composekit.component.list.item.type.SwitchListItem
import fe.linksheet.R
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.feature.libredirect.FrontendState
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.viewmodel.LibRedirectServiceSettingsViewModel
import fe.linksheet.util.web.HostUtil
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun NewLibRedirectServiceSettingsRoute(
    onBackPressed: () -> Unit,
    serviceKey: String?,
    viewModel: LibRedirectServiceSettingsViewModel = koinViewModel(parameters = {
        parametersOf(serviceKey)
    }),
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.loadSettings()
    }

    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val selectedFrontend by viewModel.selectedFrontend.collectOnIO()
    val enabled by viewModel.enabled.collectOnIO(false)

    val selected = remember(selectedFrontend) {
        selectedFrontend?.first
    }
    val selectedFrontendState = remember(selectedFrontend) { selectedFrontend?.second }

    val instances = remember(selectedFrontend) {
        selectedFrontend?.second?.instances ?: emptySet()
    }

    val resources = LocalContext.current.resources

    val serviceName = remember(settings) {
        resources.getString(R.string.lib_redirect_service, settings?.service?.name ?: "")
    }

    SaneScaffoldSettingsPage(
        headline = serviceName,
        onBackPressed = onBackPressed
    ) {
        item(key = R.string.enabled, contentType = ContentType.SingleGroupItem) {
            SwitchListItem(
                checked = enabled,
                onCheckedChange = { viewModel.updateState(it) },
                position = ContentPosition.Trailing,
                headlineContent = textContent(R.string.enabled)
            )
        }

        divider(id = R.string.frontend)

        if (selected != null && selectedFrontendState != null) {
            item(key = "dropdown", contentType = ContentType.SingleGroupItem) {
                val frontends = remember(settings) {
                    settings?.frontends ?: emptyList()
                }

                FrontendDropdown(
                    enabled = enabled,
                    selected = selectedFrontendState,
                    frontends = frontends,
                    onChange = { newFrontend -> viewModel.resetServiceToFrontend(newFrontend) }
                )
            }
        }

        divider(id = R.string.instance)

        if (selected != null) {
            group(size = instances.size + 1) {
                item(key = R.string.random_instance) { padding, shape ->
                    RadioButtonListItem(
                        shape = shape,
                        padding = padding,
                        enabled = enabled.toEnabledContentSet(),
                        selected = selected.instanceUrl == LibRedirectDefault.randomInstance,
                        onSelect = { viewModel.updateInstance(selected, LibRedirectDefault.randomInstance) },
                        position = ContentPosition.Leading,
                        headlineContent = textContent(R.string.random_instance),
                        otherContent = null
                    )
                }

                for (instance in instances) {
                    item(key = instance) { padding, shape ->
                        RadioButtonListItem(
                            shape = shape,
                            padding = padding,
                            enabled = enabled.toEnabledContentSet(),
                            selected = selected.instanceUrl == instance,
                            onSelect = { viewModel.updateInstance(selected, instance) },
                            position = ContentPosition.Leading,
                            headlineContent = text(HostUtil.cleanHttpsScheme(instance)),
                            otherContent = null
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FrontendDropdown(
    enabled: Boolean,
    selected: FrontendState,
    frontends: List<FrontendState>,
    onChange: (FrontendState) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled)
                .fillMaxWidth()
                .enabled(enabled),
            value = selected.name,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            for (frontend in frontends) {
                FrontendDropdownItem(
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
private fun FrontendDropdownItem(
    frontend: FrontendState,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(text = frontend.name) },
        onClick = onClick
    )
}
