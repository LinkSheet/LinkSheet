package fe.linksheet.composable.settings.link

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.libredirectkt.LibRedirect
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.composable.util.RadioButtonRow
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.extension.ioState
import fe.linksheet.module.viewmodel.LibRedirectServiceSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LibRedirectServiceSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: LibRedirectServiceSettingsViewModel = koinViewModel()
) {
    val service by viewModel.service.ioState()
    val selectedFrontend by viewModel.selectedFrontend.ioState()
    val selectedInstance by viewModel.selectedInstance.ioState()
    val enabled by viewModel.enabled.ioState()
    val instances by viewModel.instances.ioState()

    var expanded by remember { mutableStateOf(false) }

    val itemOnClick: (String) -> Unit = { instance ->
        val frontendKey = selectedFrontend?.key
        if (frontendKey != null && service != null) {
            viewModel.saveLibRedirectDefault(service!!.key, frontendKey, instance)
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
            stickyHeader(key = "dropdown") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    if (selectedFrontend != null && selectedInstance != null && enabled != null) {
                        SwitchRow(
                            checked = enabled!!,
                            onChange = { viewModel.updateLibRedirectState(service!!.key, it) },
                            headline = stringResource(id = R.string.enabled),
                            subtitle = null
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth()
                        ) {
                            HeadlineText(headline = R.string.frontend)

                            Spacer(modifier = Modifier.height(5.dp))

                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                TextField(
                                    value = selectedFrontend!!.name,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    service?.frontends?.forEach { frontend ->
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

                            Spacer(modifier = Modifier.height(5.dp))
                            HeadlineText(headline = R.string.instance)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }

            item {
                InstanceItem(
                    text = stringResource(id = R.string.random_instance),
                    instance = SettingsViewModel.libRedirectRandomInstanceKey,
                    selectedInstance = selectedInstance,
                    itemOnClick = itemOnClick
                )
            }

            if (instances != null) {
                items(items = instances!!, key = { it }) { instance ->
                    InstanceItem(
                        text = instance,
                        instance = instance,
                        selectedInstance = selectedInstance,
                        itemOnClick = itemOnClick
                    )
                }
            }
        }
    }
}

@Composable
fun InstanceItem(
    text: String,
    instance: String,
    selectedInstance: String?,
    itemOnClick: (String) -> Unit
) {
    RadioButtonRow(
        onClick = { itemOnClick(instance) },
        onLongClick = null,
        selected = instance == selectedInstance
    ) {
        Text(text = text)
    }
}