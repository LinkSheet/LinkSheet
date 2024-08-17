package fe.linksheet.composable.settings.link.libredirect

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.android.compose.extension.enabled
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.RadioButtonRow
import fe.linksheet.composable.util.SettingEnabledCardColumn
import fe.linksheet.composable.util.SettingSpacerText
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.viewmodel.BuiltInFrontendHolder
import fe.linksheet.module.viewmodel.LibRedirectServiceSettingsViewModel
import fe.linksheet.util.HostUtil
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibRedirectServiceSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: LibRedirectServiceSettingsViewModel = koinViewModel(),
) {
    val selected by viewModel.selected.collectOnIO()
    val enabled by viewModel.enabled.collectOnIO(false)

    SettingsScaffold(
        headline = stringResource(id = R.string.lib_redirect_service, viewModel.service.name),
        onBackPressed = onBackPressed
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(), contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "dropdown") {
                SettingEnabledCardColumn(
                    checked = enabled,
                    onChange = { viewModel.updateState(it) },
                    headline = stringResource(id = R.string.enabled),
                    subtitle = null,
                    contentTitle = stringResource(id = R.string.frontend)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    if (selected != null) {
                        FrontendDropdown(
                            enabled = enabled,
                            selected = viewModel.getFrontendByKey(selected!!.frontendKey)!!,
                            frontends = viewModel.getFrontends(),
                            onChange = { newFrontend -> viewModel.updateFrontend(selected!!, newFrontend) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    SettingSpacerText(contentTitleId = R.string.instance)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            if (selected != null) {
                item(key = "random") {
                    InstanceItem(
                        enabled = enabled,
                        text = stringResource(id = R.string.random_instance),
                        instance = LibRedirectDefault.randomInstance,
                        selected = selected!!.instanceUrl == LibRedirectDefault.randomInstance,
                        onClick = { newInstance -> viewModel.updateInstance(selected!!, newInstance) }
                    )
                }

                for (instance in viewModel.getInstancesFor(selected!!.frontendKey)!!) {
                    item(key = instance) {
                        InstanceItem(
                            enabled = enabled,
                            text = HostUtil.cleanHttpsScheme(instance),
                            instance = instance,
                            selected = selected!!.instanceUrl == instance,
                            onClick = { newInstance -> viewModel.updateInstance(selected!!, newInstance) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrontendDropdown(
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
                .menuAnchor()
                .fillMaxWidth()
                .enabled(enabled),
            value = selected.name,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            for (frontend in frontends) {
                FrontendDropdownItem(frontend = frontend, onClick = {
                    onChange(frontend.key)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun FrontendDropdownItem(
    frontend: BuiltInFrontendHolder,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(text = frontend.name) },
        onClick = onClick
    )
}


@Composable
fun InstanceItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    instance: String,
    selected: Boolean,
    onClick: (String) -> Unit,
) {
    RadioButtonRow(
        modifier = modifier,
        enabled = enabled,
        onClick = { onClick(instance) },
        onLongClick = null,
        selected = selected
    ) {
        Text(text = text)
    }
}
