package fe.linksheet.experiment.ui.overhaul.composable.page.settings.link.libredirect

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.base.ContentPosition
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.RadioButtonListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.SwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.util.Default.Companion.text
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.extension.compose.enabled
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.viewmodel.BuiltInFrontendHolder
import fe.linksheet.module.viewmodel.LibRedirectServiceSettingsViewModel
import fe.linksheet.util.HostUtil
import org.koin.androidx.compose.koinViewModel


@Composable
fun NewLibRedirectServiceSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: LibRedirectServiceSettingsViewModel = koinViewModel(),
) {
    val selected by viewModel.selected.collectOnIO()
    val enabled by viewModel.enabled.collectOnIO(false)

    val instances = remember(selected) {
        viewModel.getInstancesFor(selected?.frontendKey)
    }

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.lib_redirect_service, viewModel.service.name),
        onBackPressed = onBackPressed
    ) {
        item(key = R.string.enabled, contentType = ContentTypeDefaults.SingleGroupItem) {
            SwitchListItem(
                checked = enabled,
                onCheckedChange = { viewModel.updateState(it) },
                position = ContentPosition.Trailing,
                headlineContent = textContent(R.string.enabled)
            )
        }

        divider(stringRes = R.string.frontend)

        if (selected != null) {
            item(key = "dropdown", contentType = ContentTypeDefaults.SingleGroupItem) {
                FrontendDropdown(
                    enabled = enabled,
                    selected = viewModel.getFrontendByKey(selected!!.frontendKey)!!,
                    frontends = viewModel.getFrontends(),
                    onChange = { newFrontend -> viewModel.updateFrontend(selected!!, newFrontend) }
                )
            }
        }

        divider(stringRes = R.string.instance)

        if (selected != null) {
            group(size = instances.size + 1) {
                item(key = R.string.random_instance) { padding, shape ->
                    RadioButtonListItem(
                        shape = shape,
                        padding = padding,
                        enabled = enabled,
                        selected = selected!!.instanceUrl == LibRedirectDefault.randomInstance,
                        onSelect = { viewModel.updateInstance(selected!!, LibRedirectDefault.randomInstance) },
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
                            enabled = enabled,
                            selected = selected!!.instanceUrl == instance,
                            onSelect = { viewModel.updateInstance(selected!!, instance) },
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
    selected: BuiltInFrontendHolder,
    frontends: Iterable<BuiltInFrontendHolder>,
    onChange: (String) -> Unit,
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
                        if (selected.key != frontend.key) {
                            onChange(frontend.key)
                        }

                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun FrontendDropdownItem(
    frontend: BuiltInFrontendHolder,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(text = frontend.name) },
        onClick = onClick
    )
}
