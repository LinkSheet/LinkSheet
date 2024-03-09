package fe.linksheet.composable.settings.link.libredirect

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.RadioButtonRow
import fe.linksheet.composable.util.SettingEnabledCardColumn
import fe.linksheet.composable.util.SettingSpacerText
import fe.linksheet.extension.compose.enabled
import fe.linksheet.extension.collectOnIO
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.viewmodel.LibRedirectServiceSettingsViewModel
import fe.linksheet.util.HostUtil
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LibRedirectServiceSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: LibRedirectServiceSettingsViewModel = koinViewModel()
) {
    val service by viewModel.service.collectOnIO()
    val frontends by viewModel.frontends.collectOnIO()

    val selectedFrontend by viewModel.selectedFrontend.collectOnIO()
    val selectedInstance by viewModel.selectedInstance.collectOnIO()
    val enabled by viewModel.enabled.collectOnIO()
    val instances by viewModel.instancesForSelected.collectOnIO()

    var expanded by remember { mutableStateOf(false) }

    val itemOnClick: (String) -> Unit = { instance ->
        if (enabled!!) {
            val frontendKey = selectedFrontend?.key
            if (frontendKey != null && service != null) {
                viewModel.saveLibRedirectDefault(service!!.key, frontendKey, instance)
            }
        }
    }

    SettingsScaffold(
        headline = if (service != null) stringResource(
            id = R.string.lib_redirect_service, service!!.name
        ) else stringResource(id = R.string.lib_redirect),
        onBackPressed = onBackPressed
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            if (enabled != null) {
                stickyHeader(key = "dropdown") {

                    SettingEnabledCardColumn(
                        checked = enabled!!,
                        onChange = { viewModel.updateLibRedirectState(service!!.key, it) },
                        headline = stringResource(id = R.string.enabled),
                        subtitle = null,
                        contentTitle = stringResource(id = R.string.frontend)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (selectedFrontend != null && selectedInstance != null && frontends != null) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = {
                                    if (enabled!!) expanded = !expanded
                                }
                            ) {
                                TextField(
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                        .enabled(enabled!!),
                                    value = selectedFrontend!!.name,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    }
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    frontends?.forEach { frontend ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(text = frontend.name)
                                            },
                                            onClick = {
                                                viewModel.updateSelectedFrontend(frontend)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            SettingSpacerText(contentTitleId = R.string.instance)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }

                item(key = "random") {
                    InstanceItem(
                        enabled = enabled!!,
                        text = stringResource(id = R.string.random_instance),
                        instance = LibRedirectDefault.libRedirectRandomInstanceKey,
                        selectedInstance = selectedInstance,
                        itemOnClick = itemOnClick
                    )
                }

                if (instances != null) {
                    items(items = instances!!, key = { it }) { instance ->
                        InstanceItem(
                            enabled = enabled!!,
                            text = HostUtil.cleanHttpsScheme(instance),
                            instance = instance,
                            selectedInstance = selectedInstance,
                            itemOnClick = itemOnClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InstanceItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    instance: String,
    selectedInstance: String?,
    itemOnClick: (String) -> Unit
) {
    RadioButtonRow(
        modifier = modifier,
        enabled = enabled,
        onClick = { itemOnClick(instance) },
        onLongClick = null,
        selected = instance == selectedInstance
    ) {
        Text(text = text)
    }
}
